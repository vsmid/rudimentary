package hr.yeti.rudimentary.server.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.sql.spi.BasicDataSource;
import javax.sql.DataSource;

public class DefaultDataSource extends BasicDataSource {

    @Override
    public String id() {
        return "";
    }

    @Override
    public DataSource dataSource() {
        if (Config.provider().contains(propertyName("jdbcUrl"))) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(Config.provider().property(propertyName("jdbcUrl")).value());
            config.setDriverClassName(Config.provider().property(
                propertyName("driverClassName")
            ).value());
            config.setUsername(Config.provider().property(propertyName("username")).value());
            config.setPassword(Config.provider().property(propertyName("password")).value());
            config.setMaximumPoolSize(Config.provider().property(
                propertyName("maximumPoolSize")).asInt()
            );

            return new HikariDataSource(config);
        }
        return null;
    }

}
