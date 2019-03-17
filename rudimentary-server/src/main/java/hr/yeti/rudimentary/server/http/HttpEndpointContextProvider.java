package hr.yeti.rudimentary.server.http;

import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.mvc.spi.ViewEngine;
import hr.yeti.rudimentary.server.http.URIUtils;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpEndpointContextProvider implements Instance {

  private final Map<URI, HttpEndpoint> HTTP_ENDPOINTS = new ConcurrentHashMap<>();
  private final Map<Pattern, URI> PATTERN_PATHS_MAPPING = new ConcurrentHashMap<>();
  private Optional<ViewEngine> viewEngine = Optional.empty();

  @Override
  public void initialize() {

    Instance.providersOf(HttpEndpoint.class)
        .forEach((httpEndpoint) -> {
          duplicateURI(URIUtils.removeSlashPrefix(httpEndpoint.path()));

          HTTP_ENDPOINTS.put(URIUtils.removeSlashPrefix(httpEndpoint.path()), httpEndpoint);
          PATTERN_PATHS_MAPPING.put(URIUtils.uriAsRegex(URIUtils.removeSlashPrefix(httpEndpoint.path()).toString(), "([^/.]+)"), URIUtils.removeSlashPrefix(httpEndpoint.path()));
        });

    // TODO Should have only one engine provider, throw exception
    Instance.providersOf(ViewEngine.class)
        .forEach((engine) -> {
          viewEngine = Optional.of(engine);
        });
  }

  public void destroy() {
    Config.provider().destroy();

    HTTP_ENDPOINTS.clear();
  }

  public void registerHttpEndpoints(Class<HttpEndpoint>... endpoints) {
    Stream.of(endpoints).forEach(httpEndpoint -> {
      HttpEndpoint httpEndpointInstance;
      try {
        httpEndpointInstance = httpEndpoint.getDeclaredConstructor().newInstance();
        if (!HTTP_ENDPOINTS.containsKey(URIUtils.removeSlashPrefix(httpEndpointInstance.path()))) {
          HTTP_ENDPOINTS.put(URIUtils.removeSlashPrefix(httpEndpointInstance.path()), httpEndpointInstance);
        }
      } catch (SecurityException | IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {

      }
    });
  }

  public Optional<HttpEndpoint> getHttpEndpoint(URI path) {
    URI resolvedURI = URIUtils.removeSlashPrefix(path);

    Optional<Map.Entry<Pattern, URI>> match = PATTERN_PATHS_MAPPING.entrySet().stream()
        .filter(e -> {
          return e.getKey().asPredicate().test(URIUtils.removeSlashPrefix(path).toString());
        })
        .findFirst();
    if (match.isPresent()) {
      resolvedURI = match.get().getValue();
    }

    return Optional.ofNullable(HTTP_ENDPOINTS.get(resolvedURI));
  }

  public List<HttpEndpoint> getRegisteredUris() {
    return HTTP_ENDPOINTS.values().stream().collect(Collectors.toList());
  }

  public Optional<ViewEngine> getViewEngine() {
    return viewEngine;
  }

  private void duplicateURI(URI uri) {
    Predicate pattern = URIUtils.uriAsRegex(URIUtils.removeSlashPrefix(uri).toString(), ":.*").asPredicate();

    boolean match = HTTP_ENDPOINTS.entrySet().stream()
        .map(httpEndpoint -> httpEndpoint.getValue().path().toString())
        .anyMatch(pattern);

    if (match) {
      throw new RuntimeException("Duplicate http mapping found: " + URIUtils.removeSlashPrefix(uri).getPath());
    }
  }

  @Override
  public Class[] dependsOn() {
    return new Class[]{ HttpEndpoint.class, ViewEngine.class };
  }

}
