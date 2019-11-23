package hr.yeti.rudimentary.sql;

/**
 * Exception thrown if something goes wrong during {@link Sql#query()} or {@link Sql#query(hr.yeti.rudimentary.sql.SqlQueryDef)} method execution.
 *
 * @see Sql#query()
 * @see Sql#query(hr.yeti.rudimentary.sql.SqlQueryDef)
 *
 * @author vedransmid@yeti-it.hr
 */
public class SqlException extends RuntimeException {

    public SqlException(Throwable cause) {
        super(cause);
    }

    public SqlException(String message, Throwable cause) {
        super(message, cause);
    }

}
