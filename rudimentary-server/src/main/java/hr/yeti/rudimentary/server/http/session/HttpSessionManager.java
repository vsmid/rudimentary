package hr.yeti.rudimentary.server.http.session;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.session.Session;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO Sessions could be put to Exchange attribute
public class HttpSessionManager implements Instance {

  private Map<String, Session> sessions;
  private SecureRandom random;

  @Override
  public void destroy() {
  }

  @Override
  public void initialize() {
    try {
      sessions = new ConcurrentHashMap<>();
      random = SecureRandom.getInstance("SHA1PRNG");
    } catch (NoSuchAlgorithmException ex) {
      // Should not happen.
      Logger.getLogger(HttpSessionManager.class.getName()).log(Level.SEVERE, null, ex);
      throw new IllegalArgumentException(ex);
    }
  }

  public Session openNew() {
    String rsid = generateRSID();
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

  private String generateRSID() {
    try {
      String val = Integer.toString(random.nextInt());
      MessageDigest instance = MessageDigest.getInstance("SHA-256");
      byte[] digest = instance.digest(val.getBytes());
      return hexEncode(digest);
    } catch (NoSuchAlgorithmException ex) {
      // Should not happen.
      Logger.getLogger(HttpSessionManager.class.getName()).log(Level.SEVERE, null, ex);
      throw new IllegalArgumentException(ex);
    }
  }

  private String hexEncode(byte[] input) {
    StringBuilder result = new StringBuilder();
    char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    for (int idx = 0; idx < input.length; ++idx) {
      byte b = input[idx];
      result.append(digits[(b & 0xf0) >> 4]);
      result.append(digits[b & 0x0f]);
    }
    return result.toString();
  }

}
