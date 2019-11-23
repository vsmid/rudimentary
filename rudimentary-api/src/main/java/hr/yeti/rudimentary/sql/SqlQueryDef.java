package hr.yeti.rudimentary.sql;

/**
 * Define Sql query as a function. This comes in handy when you want to keep your Sql queries in a separate class. Think of it as a repository or DAO.
 *
 * e.g.
 *
 * <pre>
 * {@code
 * public class USER {
 *
 *  public static SqlQueryDef<Map<String, Object>> getById(long id) {
 *    return (sql) -> {
 *      return sql.row("select * from users where id=?;", id);
 *    };
 *  }
 *
 * }
 * ...
 *
 * Sql.query(USER.getById(1));
 *}
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 * @param <T> Query result.
 */
@FunctionalInterface
public interface SqlQueryDef<T> {

    T execute(Sql sql) throws SqlException;
}
