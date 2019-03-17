package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class RedirectEndpoint implements HttpEndpoint<Empty, Empty> {

  @Override
  public Empty response(Request<Empty> request) {
    return null;
  }

  @Override
  public URI redirect() {
    return URI.create("http://www.yeti-it.hr");
  }

}
