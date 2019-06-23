package hr.yeti.rudimentary.context.spi;

/**
 * Throw this exception when error occurs during {@link Context} creation or while accessing
 * its properties or executing its methods.
 *
 * @author vedransmid@yeti-it.hr
 */
public class ContextException extends RuntimeException {

  public ContextException(String message) {
    super(message);
  }

  public ContextException(String message, Throwable cause) {
    super(message, cause);
  }

}
