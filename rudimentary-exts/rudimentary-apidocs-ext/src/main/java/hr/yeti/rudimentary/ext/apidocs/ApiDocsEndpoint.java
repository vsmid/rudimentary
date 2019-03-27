package hr.yeti.rudimentary.ext.apidocs;

import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Html;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;
import java.util.Optional;

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
    return new Html("ApidDocs123");
  }

  @Override
  public Optional<String> description() {
    return Optional.of("Shows API documentation in HTML format.");
  }

}
