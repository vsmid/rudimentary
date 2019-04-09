package hr.yeti.rudimentary.server.http.session;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.filter.spi.HttpFilter;
import hr.yeti.rudimentary.http.session.Session;
import hr.yeti.rudimentary.server.http.Cookie;
import hr.yeti.rudimentary.server.http.HttpRequestUtils;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class HttpSessionCreatingFilter extends HttpFilter {

  private static final Logger LOGGER = Logger.getLogger(HttpSessionCreatingFilter.class.getName());

  private ConfigProperty createSession = new ConfigProperty("session.create");

  @Override
  public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
    Session session = null;
    Map<String, HttpCookie> cookies = HttpRequestUtils.parseCookies(exchange.getRequestHeaders());

    boolean overwriteRsidCookie = false;
    if (cookies.containsKey("RSID")) {
      try {
        session = Instance.of(HttpSessionManager.class).get(cookies.get("RSID").getValue());
      } catch (NoHttpSessionFoundException e) {
        overwriteRsidCookie = true;
        LOGGER.warning(e.getMessage());
      }
    }

    if (createSession.asBoolean() && Objects.isNull(session)) {
      HttpSession newSession = (HttpSession) Instance.of(HttpSessionManager.class).create();
      String rsid = newSession.getRsid();

      HttpCookie rsidCookie = new HttpCookie("RSID", rsid);
      rsidCookie.setHttpOnly(true);
      rsidCookie.setMaxAge(-1);

      exchange.getResponseHeaders().add("Set-Cookie", new Cookie(rsidCookie).toString());

      if (overwriteRsidCookie) {
        exchange.getRequestHeaders().get("Cookie").remove("RSID=" + cookies.get("RSID").getValue());
        exchange.getRequestHeaders().get("Cookie").add("RSID=" + rsid);

      }
    }
    chain.doFilter(exchange);
  }

  @Override
  public String description() {
    return "Filter which creates session.";
  }

  @Override
  public boolean activatingCondition() {
    return createSession.asBoolean();
  }

}
