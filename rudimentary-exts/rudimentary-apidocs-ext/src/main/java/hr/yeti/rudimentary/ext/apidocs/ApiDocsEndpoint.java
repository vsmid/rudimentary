package hr.yeti.rudimentary.ext.apidocs;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Html;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;
import java.util.Optional;
import java.util.stream.Collectors;

public class ApiDocsEndpoint implements HttpEndpoint<Empty, Html> {

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
    String titleHTML = "<h2>Rudimentary ApiDocs</h2>";
    String tableHTML = "<table><tr><td>HTTP Method</td><td>URI</td><td>HTTP Status</td><td>Description</td></tr>%s</table>";
    String rowHTML = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";

    String rowsHTML = Instance.providersOf(HttpEndpoint.class)
        .stream()
        .map(endpoint -> String.format(rowHTML, endpoint.httpMethod(), endpoint.path(), endpoint.httpStatus(), endpoint.description().orElse("")))
        .collect(Collectors.joining(System.lineSeparator()));

    return new Html(
        titleHTML + String.format(tableHTML, rowsHTML)
    );
  }

  @Override
  public Optional<String> description() {
    return Optional.of("Shows API documentation in HTML format.");
  }

}
