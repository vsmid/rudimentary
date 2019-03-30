package hr.yeti.rudimentary.exts.rudimentary.sql.sqlite.ext;

import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.sql.spi.JdbcConnectionPool;
import hr.yeti.rudimentary.pooling.ObjectPoolException;
import hr.yeti.rudimentary.pooling.ObjectPoolSettings;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SqliteJdbcConnectionPool extends JdbcConnectionPool {

  private ConfigProperty driverClass = new ConfigProperty("jdbc.driverClass");
  private ConfigProperty url = new ConfigProperty("jdbc.url");
  private ConfigProperty user = new ConfigProperty("jdbc.user");
  private ConfigProperty password = new ConfigProperty("jdbc.password");

  private ConfigProperty minSize = new ConfigProperty("jdbc.pool.minSize");
  private ConfigProperty maxSize = new ConfigProperty("jdbc.pool.maxSize");
  private ConfigProperty validationInterval = new ConfigProperty("jdbc.pool.validationInterval");
  private ConfigProperty awaitTerminationInterval = new ConfigProperty("jdbc.pool.awaitTerminationInterval");

  @Override
  public Connection createObject() throws ObjectPoolException {
    try {
      Class.forName(driverClass.toString());
      return DriverManager.getConnection(url.value(), user.value(), password.value());
    } catch (ClassNotFoundException | SQLException ex) {
      Logger.getLogger(JdbcConnectionPool.class.getName()).log(Level.SEVERE, null, ex);
      throw new ObjectPoolException("Failed to create jdbc connection pool.", ex);
    }
  }

  @Override
  protected ObjectPoolSettings settings() {
    return new ObjectPoolSettings(
        minSize.asInt(),
        maxSize.asInt(),
        validationInterval.asInt(),
        awaitTerminationInterval.asInt()
    );
  }

}
