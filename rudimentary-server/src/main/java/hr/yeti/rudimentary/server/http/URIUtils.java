package hr.yeti.rudimentary.server.http;

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
import java.util.stream.Stream;

public class URIUtils {

  public static final String PATH_VAR_STRING = ":[a-zA-Z0-9]+";
  public static final Pattern PATH_VAR_PATTERN = Pattern.compile("(" + PATH_VAR_STRING + ")");

  public static Map<String, String> parsePathVariables(URI pattern, URI uri) {
    Map<String, String> pathVariables = new HashMap<>();

    if (uri.toString().length() > 0) {
      Pattern regex = uriAsRegex(pattern.toString(), "([^/.]+)");
      Matcher regexMatcher = regex.matcher(uri.getPath());
      Matcher pathVariableMatcher = PATH_VAR_PATTERN.matcher(pattern.toString());

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

  public static Pattern uriAsRegex(String uri, String expression) {
    return Pattern.compile("^" + uri.replaceAll(PATH_VAR_STRING, expression) + "$");
  }

  public static URI removeSlashPrefix(URI uri) {
    if (uri.getPath().startsWith("/")) {
      return URI.create(uri.getPath().substring(1));
    } else {
      return URI.create(uri.getPath());
    }
  }
}
