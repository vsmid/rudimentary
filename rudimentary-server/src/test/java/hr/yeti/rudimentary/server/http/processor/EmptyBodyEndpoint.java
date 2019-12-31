package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class EmptyBodyEndpoint implements HttpEndpoint<Empty, Empty> {

  @Override
  public URI path() {
    return URI.create("emptybody");
  }

  @Override
  public Empty response(Request<Empty> request) {
    return request.getBody();
  }

}
