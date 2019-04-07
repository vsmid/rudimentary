package hr.yeti.rudimentary.server.http;

import java.net.HttpCookie;
import java.util.Objects;

public class Cookie {

  private HttpCookie httpCookie;
  private boolean sameSiteStrict;

  public Cookie(HttpCookie httpCookie) {
    this.httpCookie = httpCookie;
  }

  @Override
  public String toString() {
    return pair(httpCookie.getName(), httpCookie.getValue())
        + pair("Path", httpCookie.getPath())
        + pair("Expires", httpCookie.getMaxAge())
        + pair("Domain", httpCookie.getDomain())
        + pair("sameSite", sameSiteStrict ? "strict" : "lax")
        + (httpCookie.isHttpOnly() ? " HttpOnly;" : "")
        + (httpCookie.getSecure() ? " Secured;" : "");
  }

  private String pair(String name, Object value) {
    return String.format("%s=%s; ", name, Objects.isNull(value) ? "" : value.toString());
  }

  public void setSameSiteStrict(boolean strict) {
    this.sameSiteStrict = strict;
  }

}
