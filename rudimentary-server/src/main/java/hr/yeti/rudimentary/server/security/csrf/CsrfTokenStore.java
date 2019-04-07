package hr.yeti.rudimentary.server.security.csrf;

import hr.yeti.rudimentary.context.spi.Instance;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CsrfTokenStore implements Instance {

  private Map<String, CsrfToken> store;

  @Override
  public void initialize() {
    this.store = new ConcurrentHashMap();
  }

  @Override
  public void destroy() {
    this.store.clear();
  }

  public void remove(String csrfToken) {
    store.remove(csrfToken);
  }

  public void create() {
    CsrfToken csrfToken = new CsrfToken();
    store.put(csrfToken.getValue(), csrfToken);
  }

  public boolean contains(String csrfToken) {
    return store.containsKey(csrfToken);
  }
}
