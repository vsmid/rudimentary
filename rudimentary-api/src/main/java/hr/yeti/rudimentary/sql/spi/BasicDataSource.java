package hr.yeti.rudimentary.sql.spi;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * Class to extends when you wish to add another datasource.
 *
 * <p>
 * Since this abstract class implements {@link Instance} it means it is loaded automatically via {@link ServiceLoader} on application startup. Default datasource used by Rudimentary is registered in
 * <i><rudimentary-server</i> module in file <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.sql.spi.BasicDataSource</i>.
 * </p>
 *
 * When you add another datasource the main thing to do is to override {@link Instance#id()} method and set it to unused value. That value used must also be used in datasource configuration found in
 * configuration properties.
 *
 * For example, if we set {@link Instance#id()} to value myNewDatasource then all datasource configuration properties should look something like this:
 *
 * <pre>
 * dataSource.myNewDatasource.enabled=false
 * dataSource.myNewDatasource.driverClassName=
 * dataSource.myNewDatasource.jdbcUrl=
 * dataSource.myNewDatasource.username=
 * dataSource.myNewDatasource.password=
 * </pre>
 *
 * @see https://github.com/brettwooldridge/HikariCP for how to use and configure HikariCP.
 * @author vedransmid@yeti-it.hr
 */
public abstract class BasicDataSource implements DataSource, Instance {

    public static final String DEFAULT_DATASOURCE_ID = "";

    protected DataSource dataSource;

    @Override
    public String id() {
        return DEFAULT_DATASOURCE_ID;
    }

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

    public DataSource dataSource() {
        if (Config.provider().property(propertyName("enabled")).asBoolean()) {

            Properties dataSourceProperties = Config.provider()
                .getPropertiesByPrefix(
                    propertyName("properties"),
                    false
                );

            HikariConfig config = new HikariConfig(dataSourceProperties);

            if (Config.provider().containsNotBlank(propertyName("jdbcUrl"), true)) {
                config.setJdbcUrl(Config.provider().property(propertyName("jdbcUrl")).value());
            }

            if (Config.provider().containsNotBlank(propertyName("driverClassName"), true)) {
                config.setDriverClassName(Config.provider().property(
                    propertyName("driverClassName")
                ).value());
            }

            if (Config.provider().containsNotBlank(propertyName("username"), true)) {
                config.setUsername(Config.provider().property(propertyName("username")).value());
            }

            if (Config.provider().containsNotBlank(propertyName("password"), true)) {
                config.setPassword(Config.provider().property(propertyName("password")).value());
            }

            if (Config.provider().containsNotBlank(propertyName("maximumPoolSize"), true)) {
                config.setMaximumPoolSize(Config.provider().property(
                    propertyName("maximumPoolSize")).asInt()
                );
            }

            return new HikariDataSource(config);

        }
        return null;
    }

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
