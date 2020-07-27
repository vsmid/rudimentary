package hr.yeti.rudimentary.server.http;

import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.URIUtils;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.mvc.spi.ViewEngine;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
                URI path = URI.create(httpEndpoint.path());
                checkForDuplicateMapping(URIUtils.removeSlashPrefix(path), httpEndpoint.httpMethod());

                HTTP_ENDPOINTS.put(httpEndpoint.httpMethod() + "@" + URIUtils.removeSlashPrefix(path).toString(), httpEndpoint);
                PATTERN_PATHS_MAPPING.put(
                    URIUtils.convertToRegex(URIUtils.removeSlashPrefix(path).toString(),
                        "([^/.]+)"), URIUtils.removeSlashPrefix(path)
                );
            });
    }

    @Override
    public void destroy() {
        Config.provider().destroy();

        HTTP_ENDPOINTS.clear();
        PATTERN_PATHS_MAPPING.clear();
    }

    public HttpEndpointMatchInfo matchEndpoint(URI path, HttpMethod httpMethod) {
        boolean pathMatchFound = false;
        HttpEndpoint httpEndpoint = null;

        List<Map.Entry<Pattern, URI>> match = PATTERN_PATHS_MAPPING.entrySet().stream()
            .filter(e -> {
                return e.getKey().asPredicate().test(URIUtils.removeSlashPrefix(path).toString());
            })
            .collect(Collectors.toList());

        if (!match.isEmpty()) {
            pathMatchFound = true;

            Optional<Map.Entry<Pattern, URI>> exactMatch = match.stream()
                .filter(entry -> {
                    return entry.getValue().equals(URIUtils.removeSlashPrefix(path));
                })
                .findFirst();

            if (exactMatch.isPresent()) {
                httpEndpoint = HTTP_ENDPOINTS.get(httpMethod + "@" + exactMatch.get().getValue().toString());
            } else {
                for (Map.Entry<Pattern, URI> entry : match) {
                    httpEndpoint = HTTP_ENDPOINTS.get(httpMethod + "@" + entry.getValue().toString());
                    if (Objects.nonNull(httpEndpoint)) {
                        break;
                    }
                }
            }
        }
        return new HttpEndpointMatchInfo(pathMatchFound, httpEndpoint);
    }

    public List<HttpEndpoint> getRegisteredUris() {
        return HTTP_ENDPOINTS.values().stream()
            .sorted(Comparator.comparing((httpEndpoint) -> {
                return httpEndpoint.path();
            }))
            .collect(Collectors.toList());
    }

    private void checkForDuplicateMapping(URI uri, HttpMethod httpMethod) {
        Predicate pattern = URIUtils.convertToRegex(URIUtils.removeSlashPrefix(uri).toString(), ":.*").asPredicate();

        boolean match = HTTP_ENDPOINTS.entrySet().stream()
            .filter(httpEndpoint -> httpEndpoint.getValue().httpMethod() == httpMethod)
            .map(httpEndpoint -> URIUtils.removeSlashPrefix(URI.create(httpEndpoint.getValue().path())).toString())
            .anyMatch(pattern);

        if (match) {
            throw new RuntimeException("Duplicate http mapping found: " + URIUtils.removeSlashPrefix(uri).getPath());
        }
    }

    @Override
    public Class[] dependsOn() {
        return new Class[]{ HttpEndpoint.class, ViewEngine.class };
    }

    public static class HttpEndpointMatchInfo {

        private boolean pathMatchFound;
        private HttpEndpoint httpEndpoint;

        public HttpEndpointMatchInfo(boolean pathMatchFound, HttpEndpoint httpEndpoint) {
            this.pathMatchFound = pathMatchFound;
            this.httpEndpoint = httpEndpoint;
        }

        public boolean isPathMatchFound() {
            return pathMatchFound;
        }

        public HttpEndpoint getHttpEndpoint() {
            return httpEndpoint;
        }

    }

}
