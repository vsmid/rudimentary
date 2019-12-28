package hr.yeti.rudimentary.sql;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.sql.spi.BasicDataSource;
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

/**
 * Class used for database communication. Main goal of this class to reduce boilerplate code as much as possible and
 * just write Sql queries on the fly. Connection pool used is HikariCP.
 *
 * @see
 * <a href="https://brettwooldridge.github.io/HikariCP">https://brettwooldridge.github.io/HikariCP</a>
 * for configuration options.
 *
 * @author vedransmid@yeti-it.hr
 */
public final class Sql {

    private Connection conn;
    private boolean tx = false;

    private Sql(String dataSourceId, boolean tx) {
        BasicDataSource dataSource = Instance.withId(BasicDataSource.class, dataSourceId);

        if (Objects.nonNull(dataSource)) {
            try {
                this.conn = dataSource.getConnection();
            } catch (SQLException ex) {
                Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        return new Sql(BasicDataSource.DEFAULT_DATASOURCE_ID, false);
    }

    /**
     * Static access to Sql features.
     *
     * @param dataSourceId Id of the dataSource you want to use for the query.
     * @return A new Sql instance with specific underlying dataSource.
     */
    public static Sql query(String dataSourceId) {
        return new Sql(dataSourceId, false);
    }

    /**
     * Execute Sql transaction.By default, transaction will be rolled back for any {@link Exception}.
     *
     * @param <T> Type of result returned from transaction.
     * @param dataSourceId Id of the dataSource you want to use for the transactional query.
     * @param txDef Transaction definition as functional interface.
     * @return Transaction result.
     * @throws TransactionException
     *
     * @see TxDef
     */
    public static <T> T tx(String dataSourceId, TxDef<T> txDef) throws TransactionException {
        return (T) tx(dataSourceId, txDef, new Class[]{ Exception.class }, null);
    }

    /**
     * Execute Sql transaction. By default, transaction will be rolled back for any {@link Exception}.
     *
     * @param <T> Type of result returned from transaction.
     * @param txDef Transaction definition as functional interface.
     * @return Transaction result.
     * @throws TransactionException
     *
     * @see TxDef
     */
    public static <T> T tx(TxDef<T> txDef) throws TransactionException {
        return (T) tx(
            BasicDataSource.DEFAULT_DATASOURCE_ID, txDef, new Class[]{ Exception.class }, null
        );
    }

    /**
     * Execute Sql transaction with transaction rollback options.
     *
     * @param <T> Type of result returned from transaction.
     * @param dataSourceId Id of the dataSource you want to use for the transactional query.
     * @param txDef Transaction definition as functional interface.
     * @param rollbackOn Exceptions for which transaction will be rolled back.
     * @param noRollbackOn Exceptions for which transaction will not be rolled back. Has greater priority than @param
     * rollbackOn.
     * @return Transaction result.
     * @throws TransactionException
     *
     * @see TxDef
     */
    public static <T> T tx(
        String dataSourceId,
        TxDef<T> txDef,
        Class<? extends Exception>[] rollbackOn,
        Class<? extends Exception>[] noRollbackOn
    ) {
        Sql sql = new Sql(dataSourceId, true);
        try {
            sql.conn.setAutoCommit(false);
            T result = txDef.execute(sql);
            sql.conn.commit();
            return result;
        } catch (Throwable ex) {
            Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
            try {

                if (Objects.nonNull(rollbackOn)) {
                    boolean markedForRollback = List.<Class>of(rollbackOn).contains(ex.getClass());

                    if (Objects.nonNull(noRollbackOn)) {
                        markedForRollback &= !List.<Class>of(noRollbackOn).contains(ex.getClass());
                    }

                    if (markedForRollback) {
                        sql.conn.rollback();
                    } else {
                        sql.conn.commit();
                    }
                }
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
     * Execute Sql transaction with transaction rollback options.
     *
     * @param <T> Type of result returned from transaction.
     * @param txDef Transaction definition as functional interface.
     * @param rollbackOn Exceptions for which transaction will be rolled back.
     * @param noRollbackOn Exceptions for which transaction will not be rolled back. Has greater priority than @param
     * rollbackOn.
     * @return Transaction result.
     * @throws TransactionException
     *
     * @see TxDef
     */
    public static <T> T tx(TxDef<T> txDef, Class<? extends Exception>[] rollbackOn, Class<? extends Exception>[] noRollbackOn)
        throws TransactionException {
        return tx(BasicDataSource.DEFAULT_DATASOURCE_ID, txDef, rollbackOn, noRollbackOn);
    }

    /**
     * Execute single Sql.
     *
     * @param <T> Type of result returned.
     * @param dataSourceId Id of the dataSource you want to use for the query.
     * @param sqlDef Sql definition as functional interface.
     * @return Query result.
     *
     * @see SqlQueryDef
     */
    public static <T> T query(String dataSourceId, SqlQueryDef<T> sqlDef) {
        Sql sql = new Sql(dataSourceId, false);
        try {
            return (T) sqlDef.execute(sql);
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
        return query(BasicDataSource.DEFAULT_DATASOURCE_ID, sqlDef);
    }

}
