package hr.yeti.rudimentary.sql;

/**
 * Exception thrown if something goes wrong during Sql tx method execution.
 *
 * @see Sql#tx()
 * @see Sql#tx(hr.yeti.rudimentary.sql.TxDef)
 *
 * @author vedransmid@yeti-it.hr
 */
public class TxException extends SqlException {

  public TxException(Throwable cause) {
    super(cause);
  }

  public TxException(String message, Throwable cause) {
    super(message, cause);
  }

}
