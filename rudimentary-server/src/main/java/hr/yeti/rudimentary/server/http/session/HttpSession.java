package hr.yeti.rudimentary.server.http.session;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.session.Session;
import hr.yeti.rudimentary.server.security.crypto.Hash;
import java.util.HashMap;
import java.util.Map;

public class HttpSession implements Session {

  private String rsid;
  private long creationTime;
  private long lastAccessedTime;
  private Map<String, Object> attributes;

  public HttpSession() {
    this.rsid = Hash.generateRandomSHA256();
    this.creationTime = System.currentTimeMillis();
    this.attributes = new HashMap<>();
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
  public long getLastAccessedTime() {
    return lastAccessedTime;
  }

  public void setLastAccessedTime(long lastAccessedTime) {
    this.lastAccessedTime = lastAccessedTime;
  }

  @Override
  public void invalidate() {
    attributes.clear();
    Instance.of(HttpSessionManager.class).remove(rsid);
  }

}
