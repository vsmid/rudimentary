package hr.yeti.rudimentary.sql;

/**
 * Exception thrown if something goes wrong during {@link Sql#tx(hr.yeti.rudimentary.sql.TxDef) }
 * execution.
 *
 * @see Sql#tx(hr.yeti.rudimentary.sql.TxDef) ()
 * @see Sql#query(hr.yeti.rudimentary.sql.SqlQueryDef)
 *
 * @author vedransmid@yeti-it.hr
 */
public class TransactionException extends RuntimeException {

    public TransactionException(Throwable cause) {
        super(cause);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

}
