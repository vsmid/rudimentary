package hr.yeti.rudimentary.server;

public class ServerStartupException extends RuntimeException {

  public ServerStartupException(String msg) {
    super(msg);
  }

  public ServerStartupException(String message, Throwable cause) {
    super(message, cause);
  }

}
