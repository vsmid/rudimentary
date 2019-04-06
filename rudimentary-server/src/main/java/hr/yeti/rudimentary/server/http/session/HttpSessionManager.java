package hr.yeti.rudimentary.server.http.session;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.session.Session;
import hr.yeti.rudimentary.security.crypto.Hash;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessionManager implements Instance {

  private Map<String, Session> sessions;

  @Override
  public void destroy() {
  }

  @Override
  public void initialize() {
    sessions = new ConcurrentHashMap<>();
  }

  public Session openNew() {
    String rsid = Hash.generateRandomSHA256();
    Session newSession = new HttpSession(rsid);
    sessions.put(rsid, newSession);
    return newSession;
  }

  public Session get(String rsid) {
    if (sessions.containsKey(rsid)) {
      Session session = sessions.get(rsid);
      // TODO Check this out for something better.
      ((HttpSession) session).setLastAccessedTime(System.currentTimeMillis());
      return session;
    }
    // TODO Throw better exception.
    throw new IllegalStateException("Invalid or missing RSID!");
  }

  public void remove(String rsid) {
    sessions.remove(rsid);
  }

}
