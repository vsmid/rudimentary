package hr.yeti.rudimentary.sql;

/**
 * Define Sql transaction as a function. This comes in handy when you want to keep your transactional parts in a
 * separate class.
 *
 * e.g.
 *
 * <pre>
 * {@code
 *
 * public class USER {
 *    public static TxDef<List<Map<String, Object>>> ADD_ALL() {
 *      return (sql) -> {
 *        sql.update("insert into users(id, name) values(1, 'M');");
 *        sql.update("insert into users(id, name) values(2, 'M');");
 *        sql.update("insert into users(id, name) values(3, 'M');");
 *        return sql.rows("select * from users;");
 *      };
 *    }
 * }
 * ...
 *
 * Sql.tx(USER.ADD_ALL);
 *}
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 * @param <T> Query result.
 */
@FunctionalInterface
public interface TxDef<T> {

    T execute(Sql sql) throws SqlException;
}
