package hr.yeti.rudimentary.http;

import com.sun.net.httpserver.Headers;
import hr.yeti.rudimentary.http.session.Session;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.security.Identity;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <pre>
 * Abstraction of incoming HTTP request.
 * Offers simple way to access data received via HTTP request.
 * Heavily used in {@link HttpEndpoint} methods.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 * @param <T> Incoming HTTP request body type.
 */
public class Request<T> {

  private Identity identity;
  private Headers httpHeaders;
  private T body;
  private Map<String, String> pathVariables;
  private Map<String, String> queryParameters;
  private URI uri;
  private Session session;

  public Request(
      Identity identity,
      Headers httpHeaders,
      T body,
      Map<String, String> pathVariables,
      Map<String, String> queryParameters,
      URI uri,
      Session session) {
    this.identity = identity;
    this.httpHeaders = httpHeaders;
    this.body = body;
    this.pathVariables = pathVariables;
    this.queryParameters = queryParameters;
    this.uri = uri;
    this.session = session;
  }

  /**
   * Gets HTTP cookies received in HTTP request.
   *
   * @return List of HTTP cookies.
   */
  public List<HttpCookie> getCookies() {
    return httpHeaders.get("Cookie")
        .stream()
        .map(c -> c.split("; "))
        .flatMap(Stream::of)
        .map(HttpCookie::parse)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  /**
   * Gets user details with custom details.
   *
   * @param <D> Type of user details.
   * @param details User details class.
   * @return User identity with custom details.
   */
  public <D> Identity<D> getIdentity(Class<D> details) {
    return identity;
  }

  public Identity getIdentity() {
    return identity;
  }

  public Headers getHttpHeaders() {
    return httpHeaders;
  }

  public T getBody() {
    return body;
  }

  public Map<String, String> getPathVariables() {
    return pathVariables;
  }

  public Map<String, String> getQueryParameters() {
    return queryParameters;
  }

  public URI getUri() {
    return uri;
  }

  public Session getSession() {
    return session;
  }

}
