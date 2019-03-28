package hr.yeti.rudimentary.ext.apidocs;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Html;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.stream.Collectors;

public class ApiDocsEndpoint implements HttpEndpoint<Empty, Html> {

  private String apiDocsHTML;

  @Override
  public void initialize() {
    try {
      this.apiDocsHTML = new String(
          Thread.currentThread()
              .getContextClassLoader()
              .getResourceAsStream("templates/apidocs.html")
              .readAllBytes()
      );
    } catch (IOException ex) {
      logger().log(System.Logger.Level.ERROR, "Failed to load apidocs.html template.");
    }
  }

  @Override
  public HttpMethod httpMethod() {
    return HttpMethod.GET;
  }

  @Override
  public URI path() {
    return URI.create("/apidocs");
  }

  @Override
  public int httpStatus() {
    return 200;
  }

  @Override
  public Html response(Request<Empty> request) {
    return new Html(
        String.format(apiDocsHTML,
            rowsHTMLGenerator()
        )
    );
  }

  @Override
  public Optional<String> description() {
    return Optional.of("Shows API documentation in HTML format.");
  }

  private String rowsHTMLGenerator() {
    String rowsHTML = Instance.providersOf(HttpEndpoint.class)
        .stream()
        .map(endpoint
            -> String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
            endpoint.httpMethod(), endpoint.path(), endpoint.httpStatus(), endpoint.description().orElse("")))
        .collect(
            Collectors.joining(System.lineSeparator())
        );

    return rowsHTML;
  }

}
