package hr.yeti.rudimentary.server.http.session;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.Cookie;
import hr.yeti.rudimentary.http.HttpRequestUtils;
import hr.yeti.rudimentary.http.filter.spi.HttpFilter;
import hr.yeti.rudimentary.http.session.Session;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpSessionCreatingFilter extends HttpFilter {

  private static final Logger LOGGER = Logger.getLogger(HttpSessionCreatingFilter.class.getName());

  private ConfigProperty createSession = new ConfigProperty("session.create");

  @Override
  public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
    Session session = null;
    Map<String, HttpCookie> cookies = HttpRequestUtils.parseCookies(exchange.getRequestHeaders());

    // This part handles RSID tokens which are not stored in server's memory
    // by creating new session and overwriting unknown RSID cookie.
    boolean overwriteRsidCookie = false;
    if (cookies.containsKey(Session.COOKIE)) {

      session = (Session) exchange.getAttribute(cookies.get(Session.COOKIE).getValue());

      if (Objects.isNull(session)) {
        overwriteRsidCookie = true;
        LOGGER.log(Level.INFO, "Creating new http session with new RSID overwriting the incoming one.");
      }
    }

    if (createSession.asBoolean() && Objects.isNull(session)) {
      HttpSession newSession = new HttpSession(exchange);
      exchange.setAttribute(newSession.getRsid(), newSession);

      String rsid = newSession.getRsid();

      HttpCookie rsidCookie = new HttpCookie(Session.COOKIE, rsid);
      rsidCookie.setHttpOnly(true);
      rsidCookie.setMaxAge(-1);

      exchange.getResponseHeaders().add("Set-Cookie", new Cookie(rsidCookie).toString());

      List<String> rawRequestCookies = exchange.getRequestHeaders().get("Cookie");
      if (overwriteRsidCookie) {
        rawRequestCookies.clear();
        rawRequestCookies.add(Session.COOKIE + "=" + rsid);

      }
    }
    chain.doFilter(exchange);
  }

  @Override
  public String description() {
    return "Filter which creates session.";
  }

  @Override
  public boolean conditional() {
    return createSession.asBoolean();
  }

}
