package hr.yeti.rudimentary.server.http;

import com.sun.net.httpserver.Headers;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Model;
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
import static hr.yeti.rudimentary.server.http.URIUtils.convertToRegex;
import java.lang.reflect.ParameterizedType;

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

  public static Map<String, String> parsePathVariables(URI pattern, URI uri) {
    Map<String, String> pathVariables = new HashMap<>();

    if (uri.toString().length() > 0) {
      Pattern regex = convertToRegex(pattern.toString(), "([^/.]+)");
      Matcher regexMatcher = regex.matcher(uri.getPath());
      Matcher pathVariableMatcher = URIUtils.PATH_VAR_PATTERN.matcher(pattern.toString());

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
}
