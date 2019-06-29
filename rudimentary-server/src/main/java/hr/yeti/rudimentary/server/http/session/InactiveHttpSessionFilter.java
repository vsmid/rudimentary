package hr.yeti.rudimentary.server.http.session;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.Cookie;
import hr.yeti.rudimentary.http.HttpRequestUtils;
import hr.yeti.rudimentary.http.filter.spi.HttpFilter;
import hr.yeti.rudimentary.http.session.Session;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InactiveHttpSessionFilter extends HttpFilter {

  private static final Logger LOGGER = Logger.getLogger(InactiveHttpSessionFilter.class.getName());

  private ConfigProperty createSession = new ConfigProperty("session.create");
  private ConfigProperty inactivityPeriodAllowed = new ConfigProperty("session.inactivityPeriodAllowed");

  @Override
  public boolean conditional() {
    return createSession.asBoolean();
  }

  @Override
  public String description() {
    return "Filter which checks whether session was inactive for a preconfigured period of time and then invalidates if so.";
  }

  @Override
  public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
    Map<String, HttpCookie> cookies = HttpRequestUtils.parseCookies(exchange.getRequestHeaders());

    String RSID;

    // Try response headers
    if (cookies.isEmpty() || !cookies.containsKey(Session.COOKIE)) {
      RSID = exchange.getResponseHeaders().get("Set-Cookie").get(0).substring(5).split(";")[0];
    } else {
      RSID = cookies.get(Session.COOKIE).getValue();
    }

    Session session = (Session) exchange.getAttribute(RSID);

    long lastAccessedTime = session.getLastAccessedTime();
    long currentTime = System.currentTimeMillis();

    if ((currentTime - lastAccessedTime) / (1_000 * 60) > inactivityPeriodAllowed.asInt()) {
      LOGGER.log(Level.WARNING, "Session with RSID={0} has expired after period of inactivity.", RSID);

      // Inavlidate and remove session.
      session.invalidate();

      // Delete RSID cookie
      HttpCookie rsidCookie = new HttpCookie(Session.COOKIE, exchange.getResponseHeaders().get("Set-Cookie").get(0));
      rsidCookie.setMaxAge(0);

      exchange.sendResponseHeaders(440, 0);
      exchange.getResponseHeaders().add("Set-Cookie", new Cookie(rsidCookie).toString());
      exchange.close();

      return;
    }

    chain.doFilter(exchange);
  }

}
