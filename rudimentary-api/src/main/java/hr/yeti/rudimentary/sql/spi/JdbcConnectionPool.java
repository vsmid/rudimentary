package hr.yeti.rudimentary.sql.spi;

import hr.yeti.rudimentary.pooling.spi.ObjectPool;
import hr.yeti.rudimentary.sql.Sql;
import java.sql.Connection;

/**
 * Specialized {@link ObjectPool} SPI for providing JDBC connection pool.
 *
 * Since this abstract class inherently extends {@link Instance} it means it is loaded automatically
 * via {@link ServiceLoader} on application startup.
 *
 * You can have as many different JdbcConnectionPool providers as you want and you can register
 * them in
 * <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.sql.spi.JdbcConnectionPool</i>
 * file. Currently however, the one marked as priority one or the first one listed will used.
 *
 * By extending this class you basically provide a way to communicate with the database. An example
 * of one such implementation can be seen in
 * <b>rudimentary/rudimentary-exts/rudimentary-sql-sqlite-ext</b> module.
 *
 * This is then directly used by the {@link Sql} to handle JDBC connections.
 *
 * @author vedransmid@yeti-it.hr
 */
public abstract class JdbcConnectionPool extends ObjectPool<Connection> {

}
