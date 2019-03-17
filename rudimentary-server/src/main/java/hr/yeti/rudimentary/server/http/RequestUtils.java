package hr.yeti.rudimentary.server.http;

import com.sun.net.httpserver.Headers;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestUtils {

  public static Map<String, HttpCookie> cookies(Headers headers) {
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

}
