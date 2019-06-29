package hr.yeti.rudimentary.http.session;

import java.util.Map;

/**
 * An abstraction of HTTP session. Mostly used in MVC.
 *
 * @see <b>rudimentary/rudimentary-server</b> module for details about how RSID session cookie is
 * generated in {@code HttpSessionCreatingFilter} class.
 *
 * @author vedransmid@yeti-it.hr
 */
public interface Session {

  /**
   * Http cookie name used to identify user's session.
   */
  public static final String COOKIE = "RSID";

  /**
   * Get unique session id value which uniquely identifies session.
   *
   * @return Unique session id value.
   */
  String getRsid();

  /**
   * Get session creation time.
   *
   * @return Time of session creation.
   */
  long getCreationTime();

  /**
   * Get map of session attributes. Here you can store values to be used during HTTP session.
   *
   * @return Map of session attributes.
   */
  Map<String, Object> getAttributes();

  /**
   * Get time of last session access.
   *
   * @return Time of last session access.
   */
  long getLastAccessedTime();

  /**
   * Destroy current session.
   */
  void invalidate();

  /**
   * Get csrf token value.
   *
   * @return Csrf token value.
   */
  String getCsrfToken();
  
  /**
   *
   * @return Whether or not user request has been successfully authenticated.
   */
  boolean isAuthenticated();

}
