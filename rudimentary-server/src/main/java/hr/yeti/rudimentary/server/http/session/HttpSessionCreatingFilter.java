package hr.yeti.rudimentary.server.http.session;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.filter.spi.HttpFilter;
import hr.yeti.rudimentary.server.http.Cookie;
import hr.yeti.rudimentary.server.http.HttpRequestUtils;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.Map;

public class HttpSessionCreatingFilter extends HttpFilter {

  private ConfigProperty createSession = new ConfigProperty("session.create");

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
