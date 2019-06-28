package hr.yeti.rudimentary.security.spi;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.security.Identity;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * SPI providing user authentication mechanism.
 *
 * Since this interface implements {@link Instance} it means it is loaded automatically via
 * {@link ServiceLoader} on application startup.
 *
 * There should be only one AuthMechanism provider per application.
 *
 * You can register it in
 * <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.security.spi.AuthMechanism</i>
 * file.
 *
 * Rudimentary currently provides BasicAuth extension module where you can see how to implement
 * custom authentication provider. You can find it under
 * <b>rudimentary/rudimentary-exts/rudimentary-security-auth-basic-ext</b>.
 *
 * @author vedransmid@yeti-it.hr
 */
public abstract class AuthMechanism extends Authenticator implements Instance {

  protected ConfigProperty realm = new ConfigProperty("security.realm");
  protected ConfigProperty urisRequiringAuthentication = new ConfigProperty("security.urisRequiringAuthentication");
  protected ConfigProperty urisNotRequiringAuthentication = new ConfigProperty("security.urisNotRequiringAuthentication");

  /**
   * A list of secured URI's in Pattern format.
   */
  protected List<Pattern> urisRequiringAuthenticationCache = new ArrayList<>();

  /**
   * A list of non secured URI's in Pattern format.
   */
  protected List<Pattern> urisNotRequiringAuthenticationCache = new ArrayList<>();

  /**
   * Set whether authentication mechanism be enabled or not.
   *
   * @return true if authentication mechanism is enabled, otherwise false.
   */
  public abstract boolean enabled();

  /**
   * Set an array of string based URI's which require authentication. URI's should be in
   * {@link Pattern} compatible format. Internally, during authentication each URI is treated as
   * Pattern to see whether it matches incoming HTTP request URI. If match, authentication is
   * performed.
   *
   * @return An array of strings representing URI's which require authentication.
   */
  public String[] urisRequiringAuthentication() {
    return urisRequiringAuthentication.asArray();
  }

  /**
   * Set an array of string based URI's which do not require authentication. URI's should be in
   * {@link Pattern} compatible format. Internally, during authentication each URI is treated as
   * Pattern to see whether it matches incoming HTTP request URI. If match, no authentication will
   * be executed.
   *
   * @return An array of strings representing URI's which require authentication.
   */
  public String[] urisNotRequiringAuthentication() {
    return urisNotRequiringAuthentication.asArray();
  }

  /**
   * Implement authentication mechanism.
   *
   * @param exchange Incoming HTTP request in the form of {@link HttpExchange}.
   * @return Authentication result.
   */
  public abstract Result doAuth(HttpExchange exchange);

  /**
   * Implement user data retrieval.
   *
   * @see rudimentary/rudimentary-exts/rudimentary-security-auth-basic-ext module for the usage of
   * {@link IdentityStore}.
   *
   * @see rudimentary/rudimentary-exts/rudimentary-security-identitystore-embedded-ext module for
   * the implementation of {@link IdentityStore}.
   *
   * @param principal Current user in the form of {@link HttpPrincipal}.
   * @return Fully identified user with details which will be available through
   * {@link Request#getIdentity()} in {@link HttpEndpoint}.
   */
  public abstract Identity getIdentity(HttpPrincipal principal);

  // TODO Add sessions to exchange?, use it to prevent auth on every call for session apps, use RSID as validation mechanism
  /**
   * A method which is being called internally to execute authentication. This method should not be
   * used unless you really know what you are doing. This method internally calls
   * {@link AuthMechanism#doAuth(com.sun.net.httpserver.HttpExchange)} method.
   *
   * If URI does not require authentication or authentication is disabled, a user with default name
   * of 'anonymous' is used as principal.
   *
   * @param exchange Incoming HTTP request in the form of {@link HttpExchange}.
   * @return Authentication result.
   */
  @Override
  public Result authenticate(HttpExchange exchange) {
    if (enabled()) {
      if (requiresAuthentication(exchange.getRequestURI())) {
        Result result = doAuth(exchange);

        if (result instanceof Success) {
          // Set principal as identity with full identity info
          Identity identity = getIdentity(((Success) result).getPrincipal());
          result = new Authenticator.Success(identity);
        }

        return result;
      } else {
        return new Success(new Identity("anonymous", ""));
      }
    } else {
      return new Success(new Identity("anonymous", ""));
    }
  }

  /**
   * Checks whether incoming URI requires authentication or not.
   *
   * @param uri URI of the incoming HTTP request.
   * @return true if URI requires authentication, otherwise false.
   */
  protected boolean requiresAuthentication(URI uri) {
    return uriFoundInCache(uri, urisRequiringAuthenticationCache) && !uriFoundInCache(uri, urisNotRequiringAuthenticationCache);
  }

  /**
   * Creates cache out of URI's by converting them to {@link Pattern}.
   */
  protected void cacheUrisAsPatterns(String[] uris, List<Pattern> cache) {
    if (cache.isEmpty()) {
      Stream.of(uris)
          .map(Pattern::compile)
          .forEach(cache::add);
    }
  }

  /**
   * Checks whether @param uri is contained within @param cache.
   *
   * @param uri
   * @param cache List containing uris as {@link Pattern}.
   * @return
   */
  protected boolean uriFoundInCache(URI uri, List<Pattern> cache) {
    return cache
        .stream()
        .anyMatch((pattern) -> {
          return pattern.asPredicate().test(uri.getPath());
        });
  }

  @Override
  public void initialize() {
    cacheUrisAsPatterns(urisRequiringAuthentication(), urisRequiringAuthenticationCache);
    cacheUrisAsPatterns(urisNotRequiringAuthentication(), urisNotRequiringAuthenticationCache);
  }

}
