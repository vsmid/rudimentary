package hr.yeti.rudimentary.server.http;

import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.mvc.spi.ViewEngine;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpEndpointContextProvider implements Instance {

  private final Map<String, HttpEndpoint> HTTP_ENDPOINTS = new ConcurrentHashMap<>();
  private final Map<Pattern, URI> PATTERN_PATHS_MAPPING = new ConcurrentHashMap<>();

  @Override
  public void initialize() {

    Instance.providersOf(HttpEndpoint.class)
        .forEach((httpEndpoint) -> {
          checkForDuplicateMapping(URIUtils.removeSlashPrefix(httpEndpoint.path()), httpEndpoint.httpMethod());

          HTTP_ENDPOINTS.put(httpEndpoint.httpMethod() + "@" + URIUtils.removeSlashPrefix(httpEndpoint.path()).toString(), httpEndpoint);
          PATTERN_PATHS_MAPPING.put(
              URIUtils.uriAsRegex(URIUtils.removeSlashPrefix(httpEndpoint.path()).toString(),
                  "([^/.]+)"), URIUtils.removeSlashPrefix(httpEndpoint.path())
          );
        });
  }

  @Override
  public void destroy() {
    Config.provider().destroy();

    HTTP_ENDPOINTS.clear();
  }

  public Optional<HttpEndpoint> getHttpEndpoint(URI path, HttpMethod httpMethod) {
    URI resolvedURI = URIUtils.removeSlashPrefix(path);

    Optional<Map.Entry<Pattern, URI>> match = PATTERN_PATHS_MAPPING.entrySet().stream()
        .filter(e -> {
          return e.getKey().asPredicate().test(URIUtils.removeSlashPrefix(path).toString());
        })
        .findFirst();
    if (match.isPresent()) {
      resolvedURI = match.get().getValue();
    }

    return Optional.ofNullable(HTTP_ENDPOINTS.get(httpMethod + "@" + resolvedURI.toString()));
  }

  public List<HttpEndpoint> getRegisteredUris() {
    return HTTP_ENDPOINTS.values().stream()
        .sorted(Comparator.comparing((httpEndpoint) -> {
          return httpEndpoint.path().toString();
        }))
        .collect(Collectors.toList());
  }

  private void checkForDuplicateMapping(URI uri, HttpMethod httpMethod) {
    Predicate pattern = URIUtils.uriAsRegex(URIUtils.removeSlashPrefix(uri).toString(), ":.*").asPredicate();

    boolean match = HTTP_ENDPOINTS.entrySet().stream()
        .filter(httpEndpoint -> httpEndpoint.getValue().httpMethod() == httpMethod)
        .map(httpEndpoint -> URIUtils.removeSlashPrefix(httpEndpoint.getValue().path()).toString())
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
