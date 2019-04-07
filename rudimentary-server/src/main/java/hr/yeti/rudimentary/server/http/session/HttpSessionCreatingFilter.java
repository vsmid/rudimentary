package hr.yeti.rudimentary.server.http.session;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.filter.spi.HttpFilter;
import hr.yeti.rudimentary.server.http.Cookie;
import hr.yeti.rudimentary.server.http.HttpRequestUtils;
import hr.yeti.rudimentary.server.security.csrf.CsrfToken;
import hr.yeti.rudimentary.server.security.csrf.CsrfTokenStore;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.Map;

public class HttpSessionCreatingFilter extends HttpFilter {

  private ConfigProperty createSession = new ConfigProperty("session.create");
  private ConfigProperty csrfEnabled = new ConfigProperty("security.csrf.enabled");
  private ConfigProperty csrfTokenCookieName = new ConfigProperty("security.csrf.tokenCookieName");

  @Override
  public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
    Map<String, HttpCookie> cookies = HttpRequestUtils.parseCookies(exchange.getRequestHeaders());
    if (createSession.asBoolean() && !cookies.containsKey("RSID")) {
      HttpSession newSession = (HttpSession) Instance.of(HttpSessionManager.class).create();
      String rsid = newSession.getRsid();

      HttpCookie rsidCookie = new HttpCookie("RSID", rsid);
      rsidCookie.setHttpOnly(true);
      rsidCookie.setMaxAge(-1);
      exchange.getResponseHeaders().add("Set-Cookie", new Cookie(rsidCookie).toString());

      if (csrfEnabled.asBoolean()) {
        // TODO Only generate token for non state changing request - this part handles the first request
        // which should be get for web apps.
        CsrfToken csrfToken = Instance.of(CsrfTokenStore.class).create();
        newSession.setCsrfToken(csrfToken);
        HttpCookie cookie = new HttpCookie(csrfTokenCookieName.value(), newSession.getCsrfToken());
        cookie.setHttpOnly(false);
        cookie.setMaxAge(-1);
        exchange.getResponseHeaders().add("Set-Cookie", new Cookie(cookie).toString());
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

  @Override
  public Class[] dependsOn() {
    return new Class[]{ CsrfTokenStore.class };
  }

}
