package hr.yeti.rudimentary.sql.spi;

import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Instance;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ConnectionBuilder;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.ShardingKeyBuilder;
import java.util.Objects;
import java.util.logging.Logger;
import javax.sql.DataSource;

// TODO Add Javadoc
public abstract class BasicDataSource implements DataSource, Instance {

    public static final String DEFAULT_DATASOURCE_ID = "";

    protected DataSource dataSource;

    @Override
    public void initialize() {
        this.dataSource = dataSource();
    }

    @Override
    public boolean conditional() {
        ConfigProperty enabled = Config.provider()
                .property("dataSource" + (id().length() == 0 ? "" : ".") + id() + ".enabled");

        return Objects.nonNull(enabled) && enabled.asBoolean();
    }

    public abstract DataSource dataSource();

    public String propertyName(String property) {
        return "dataSource." + (id().length() == 0 ? "" : id() + ".") + property;
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
