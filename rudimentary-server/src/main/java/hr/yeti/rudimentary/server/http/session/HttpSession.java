package hr.yeti.rudimentary.server.http.session;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.Cookie;
import hr.yeti.rudimentary.http.session.Session;
import hr.yeti.rudimentary.security.Identity;
import hr.yeti.rudimentary.server.crypto.Hash;
import hr.yeti.rudimentary.server.security.csrf.CsrfToken;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpSession implements Session {

  private String rsid;
  private long creationTime;
  private long lastAccessedTime;
  private Map<String, Object> attributes;
  private CsrfToken csrfToken;
  private boolean authenticated;
  private Identity identity;

  public HttpSession() {
    this.rsid = Hash.generateRandomSHA256();
    this.creationTime = System.currentTimeMillis();
    this.lastAccessedTime = this.creationTime;
    this.attributes = new HashMap<>();
    this.authenticated = false;
  }

  @Override
  public String getRsid() {
    return rsid;
  }

  @Override
  public long getCreationTime() {
    return creationTime;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public Optional<Object> getAttribute(String name) {
    Object attribute = null;

    if (Objects.nonNull(attributes)) {
      attribute = attributes.get(name);
    }

    return Optional.ofNullable(attribute);
  }

  @Override
  public long getLastAccessedTime() {
    return lastAccessedTime;
  }

  public void setLastAccessedTime(long lastAccessedTime) {
    this.lastAccessedTime = lastAccessedTime;
  }

  @Override
  public void invalidate(HttpExchange exchange) {
    this.attributes = null;
    this.csrfToken = null;
    this.creationTime = 0;
    this.lastAccessedTime = 0;
    this.authenticated = false;
    this.identity = null;
    this.attributes.clear();

    HttpCookie rsidCookie = new HttpCookie(Session.COOKIE, this.rsid);
    rsidCookie.setMaxAge(0);

    exchange.getResponseHeaders().add("Set-Cookie", new Cookie(rsidCookie).toString());
    exchange.setAttribute(rsid, null);
  }

  @Override
  public boolean isAuthenticated() {
    return this.authenticated;
  }

  public void setAuthenticated(boolean authenticated) {
    this.authenticated = authenticated;
  }

  @Override
  public Identity getIdentity() {
    return identity;
  }

  @Override
  public <D> Identity<D> getIdentity(Class<D> details) {
    return identity;
  }

  public void setIdentity(Identity identity) {
    this.identity = identity;
  }

}
