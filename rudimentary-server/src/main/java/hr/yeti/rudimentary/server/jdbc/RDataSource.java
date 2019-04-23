package hr.yeti.rudimentary.server.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Instance;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ConnectionBuilder;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.ShardingKeyBuilder;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class RDataSource implements DataSource, Instance {

  private HikariDataSource dataSource;

  @Override
  public void initialize() {
    if (Config.provider().contains("dataSource.jdbcUrl")) {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(Config.provider().property("dataSource.jdbcUrl").value());
      config.setDriverClassName(Config.provider().property("dataSource.driverClassName").value());
      config.setUsername(Config.provider().property("dataSource.username").value());
      config.setPassword(Config.provider().property("dataSource.password").value());
      config.setMaximumPoolSize(Config.provider().property("dataSource.maximumPoolSize").asInt());

      dataSource = new HikariDataSource(config);
    }
  }

  @Override
  public Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return dataSource.getConnection(username, password);
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return dataSource.getLogWriter();
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    dataSource.setLogWriter(out);
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    dataSource.setLoginTimeout(seconds);
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return dataSource.getLoginTimeout();
  }

  @Override
  public ConnectionBuilder createConnectionBuilder() throws SQLException {
    return dataSource.createConnectionBuilder();
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return dataSource.getParentLogger();
  }

  @Override
  public ShardingKeyBuilder createShardingKeyBuilder() throws SQLException {
    return dataSource.createShardingKeyBuilder();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return dataSource.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return dataSource.isWrapperFor(iface);
  }

}
