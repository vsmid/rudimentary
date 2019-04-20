package hr.yeti.rudimentary.sql;

import hr.yeti.rudimentary.context.spi.Instance;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * Class used for static access to database communication. Main goal of this class to reduce boilerplate code as much as possible and just write Sql queries on the fly.
 *
 * @author vedransmid@yeti-it.hr
 */
public final class Sql {

  private Connection conn;
  private boolean tx = false;

  private Sql(boolean tx) {
    try {
      // TODO Add filter when there will be multiple pools, for now use only one provider.
      // TODO Throw exception explaining how jdbc conn. pool is not configured if conn == null
      this.conn = Instance.of(DataSource.class).getConnection();
    } catch (SQLException ex) {
      Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
    }
    this.tx = tx;
  }

  /**
   * Retrieve a list of results from database.
   *
   * @param sql Sql query.
   * @param params Ordered Sql query parameters.
   * @return A list of objects.
   */
  public List<Map<String, Object>> rows(String sql, Object... params) {
    List<Map<String, Object>> result = new ArrayList<>();

    Statement statement = null;
    ResultSet resultSet = null;

    try {
      boolean dynamic = Objects.nonNull(params) && params.length > 0;
      statement = dynamic ? conn.prepareStatement(sql) : conn.createStatement();
      if (dynamic) {
        int paramIndex = 1;
        for (Object param : params) {
          ((PreparedStatement) statement).setObject(paramIndex++, param);
        }
        resultSet = ((PreparedStatement) statement).executeQuery();
      } else {
        resultSet = statement.executeQuery(sql);
      }
      ResultSetMetaData metaData = resultSet.getMetaData();
      while (resultSet.next()) {
        Map<String, Object> map = new HashMap<>(metaData.getColumnCount());
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
          map.put(metaData.getColumnName(i), resultSet.getObject(metaData.getColumnName(i)));
        }
        result.add(map);
      }
    } catch (SQLException ex) {
      throw new SqlException(null, ex);
    } finally {
      try {
        if (Objects.nonNull(resultSet) && Objects.nonNull(statement)) {
          resultSet.close();
          statement.close();
        }
        if (!tx) {
          conn.close();
        }
      } catch (SQLException ex) {
        Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    return result;
  }

  /**
   * Retrieve a single result from database.
   *
   * @param sql Sql query.
   * @param params Ordered Sql query parameters.
   * @return A list of objects.
   */
  public Map<String, Object> row(String sql, Object... params) {
    Map<String, Object> map = Map.of();

    Statement statement = null;
    ResultSet resultSet = null;

    try {
      boolean dynamic = Objects.nonNull(params) && params.length > 0;
      statement = dynamic ? conn.prepareStatement(sql) : conn.createStatement();

      if (dynamic) {
        int paramIndex = 1;
        for (Object param : params) {
          ((PreparedStatement) statement).setObject(paramIndex++, param);
        }
        resultSet = ((PreparedStatement) statement).executeQuery();
      } else {
        resultSet = statement.executeQuery(sql);
      }
      ResultSetMetaData metaData = resultSet.getMetaData();

      boolean found = resultSet.next();
      if (found) {
        map = new HashMap<>(1);
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
          map.put(metaData.getColumnName(i), resultSet.getObject(metaData.getColumnName(i)));
        }
      }
    } catch (SQLException ex) {
      throw new SqlException(null, ex);
    } finally {
      try {
        if (Objects.nonNull(resultSet) && Objects.nonNull(statement)) {
          resultSet.close();
          statement.close();
        }
        if (!tx) {
          conn.close();
        }
      } catch (SQLException ex) {
        Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    return map;
  }

  /**
   * Insert, update or delete rows from database.
   *
   * @param sql Sql query.
   * @param params Ordered Sql query parameters.
   * @return A list of objects.
   */
  public long update(String sql, Object... params) {
    long updated;
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = conn.prepareStatement(sql);
      int paramIndex = 1;
      for (Object param : params) {
        preparedStatement.setObject(paramIndex++, param);
      }
      updated = preparedStatement.executeUpdate();
    } catch (SQLException ex) {
      throw new SqlException(null, ex);
    } finally {
      try {
        if (Objects.nonNull(preparedStatement)) {
          preparedStatement.close();
        }
        if (!tx) {
          conn.close();
        }
      } catch (SQLException ex) {
        Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    return updated;
  }

  /**
   * Static access to Sql features.
   *
   * @return A new Sql instance.
   */
  public static Sql query() {
    return new Sql(false);
  }

  // TODO Add rollbackOn options...
  /**
   * Execute Sql transaction.
   *
   * @param <T> Type of result returned from transaction.
   * @param txDef Transaction definition as functional interface.
   * @return Transaction result.
   *
   * @see TxDef
   */
  public static <T> T tx(TxDef<T> txDef) {
    Sql sql = new Sql(true);
    try {
      sql.conn.setAutoCommit(false);
      T result = txDef.execute(sql);
      sql.conn.commit();
      return result;
    } catch (Throwable ex) {
      Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
      try {
        sql.conn.rollback();
      } catch (SQLException ex1) {
        Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex1);
        throw new TxException(ex);
      }
      throw new TxException(ex);
    } finally {
      try {
        sql.conn.close();
      } catch (Exception ex) {
        Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
        throw new TxException(ex);
      }
    }
  }

  /**
   * Execute single Sql.
   *
   * @param <T> Type of result returned.
   * @param sqlDef Sql definition as functional interface.
   * @return Query result.
   *
   * @see SqlQueryDef
   */
  public static <T> T query(SqlQueryDef<T> sqlDef) {
    Sql sql = new Sql(false);
    try {
      return (T) sqlDef.execute(new Sql(true));
    } catch (Throwable ex) {
      Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
      throw new SqlException(ex);
    } finally {
      try {
       sql.conn.close();
      } catch (Exception ex) {
        Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
        throw new SqlException(ex);
      }
    }
  }

}
