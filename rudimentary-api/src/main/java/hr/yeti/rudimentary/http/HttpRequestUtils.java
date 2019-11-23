package hr.yeti.rudimentary.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import static hr.yeti.rudimentary.http.URIUtils.convertToRegex;
import hr.yeti.rudimentary.http.content.Model;
import hr.yeti.rudimentary.http.session.Session;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;

public class HttpRequestUtils {

    public static Map<String, HttpCookie> parseCookies(Headers headers) {
        if (!headers.containsKey("Cookie")) {
            return Map.of();
        }

        return headers.get("Cookie")
                .stream()
                .map(c -> c.split("; "))
                .flatMap(Stream::of)
                .map(HttpCookie::parse)
                .flatMap(List::stream)
                .collect(Collectors.toMap((c) -> {
                    return c.getName();
                }, (c) -> {
                    return c;
                }));
    }

    public static Map<String, String> parsePathVariables(URI endpointPath, URI uri) {
        Map<String, String> pathVariables = new HashMap<>();

        URI normalizedPath = URIUtils.prependSlashPrefix(endpointPath);

        if (uri.toString().length() > 0) {
            Pattern regex = convertToRegex(normalizedPath.toString(), "([^/.]+)");
            Matcher regexMatcher = regex.matcher(uri.getPath());
            Matcher pathVariableMatcher = URIUtils.PATH_VAR_PATTERN.matcher(normalizedPath.toString());

            List<String> list = new ArrayList<>();
            while (pathVariableMatcher.find()) {
                list.add(pathVariableMatcher.group(1).substring(1));
            }

            while (regexMatcher.find()) {
                for (int i = 0; i <= regexMatcher.groupCount() - 1; i++) {
                    pathVariables.put(list.get(i), regexMatcher.group(i + 1));
                }
            }
        }

        return pathVariables;
    }

    public static Map<String, Object> parseQueryParameters(String query) {
        Map<String, Object> queryParameters = new HashMap<>();

        if (Objects.nonNull(query) && query.length() > 0) {
            Stream.of(query.split("&"))
                    .forEach(pair -> {
                        String[] keyValue = pair.split("=");
                        queryParameters.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
                    });
        }

        return queryParameters;
    }

    public static Class<? extends Request<? extends Model>> getRequestBodyType(Class<?> clazz) throws ClassNotFoundException {
        try {
            String className = ((ParameterizedType) clazz.getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName();
            return (Class<? extends Request<? extends Model>>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class is not parametrized with generic type.", e);
        }
    }

    public static Optional<Session> extractSession(HttpExchange exchange) {
        Map<String, HttpCookie> cookies = HttpRequestUtils.parseCookies(exchange.getRequestHeaders());
        if (cookies.isEmpty()) {
            return Optional.empty();
        } else {
            Session session = (Session) exchange.getAttribute(cookies.get(Session.COOKIE).getValue());
            return Optional.ofNullable(session);
        }
    }
}
