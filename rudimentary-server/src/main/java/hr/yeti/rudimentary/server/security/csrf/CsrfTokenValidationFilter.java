package hr.yeti.rudimentary.server.security.csrf;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.filter.spi.HttpFilter;
import java.io.IOException;

public class CsrfTokenValidationFilter extends HttpFilter {

  private ConfigProperty csrfEnabled = new ConfigProperty("security.csrf.enabled");
  private ConfigProperty csrfStateless = new ConfigProperty("security.csrf.stateless");
  private ConfigProperty csrfTokenHttpHeaderName = new ConfigProperty("security.csrf.tokenHttpHeaderName");
  private ConfigProperty csrfTokenCookieName = new ConfigProperty("security.csrf.tokenCookieName");

  @Override
  public boolean activatingCondition() {
    return csrfEnabled.asBoolean();
  }

  @Override
  public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
    
  }

  @Override
  public String description() {
    return "Filter which validates CSRF token.";
  }

}
