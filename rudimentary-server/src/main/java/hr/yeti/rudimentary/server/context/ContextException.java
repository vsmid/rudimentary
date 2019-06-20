package hr.yeti.rudimentary.server.context;

public class ContextException extends RuntimeException {

  public ContextException(String message) {
    super(message);
  }

  public ContextException(String message, Throwable cause) {
    super(message, cause);
  }

}
