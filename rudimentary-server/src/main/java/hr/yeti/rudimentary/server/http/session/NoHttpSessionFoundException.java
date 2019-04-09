package hr.yeti.rudimentary.server.http.session;

public class NoHttpSessionFoundException extends RuntimeException {

  public NoHttpSessionFoundException() {
  }

  public NoHttpSessionFoundException(String msg) {
    super(msg);
  }
}
