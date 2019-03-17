package hr.yeti.rudimentary.server.http.session;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.server.http.RequestUtils;
import hr.yeti.rudimentary.session.Session;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.Map;

public class HttpSessionCreatingFilter extends Filter {

  private ConfigProperty createSession = new ConfigProperty("session.create");

  @Override
  public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
    Map<String, HttpCookie> cookies = RequestUtils.cookies(exchange.getRequestHeaders());
    if (createSession.asBoolean() && !cookies.containsKey("RSID")) {
      Session newSession = Instance.of(HttpSessionManager.class).openNew();
      String rsid = newSession.getRsid();
      HttpCookie cookie = new HttpCookie("RSID", rsid);
      cookie.setHttpOnly(true);
      cookie.setMaxAge(-1);
      exchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
    }
    chain.doFilter(exchange);
  }

  @Override
  public String description() {
    return "Filter which creates session.";
  }

}
