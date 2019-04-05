package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Form;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.mvc.spi.ViewEndpoint;
import java.net.URI;

public class CustomViewEndpoint implements ViewEndpoint<Form> {

  @Override
  public URI path() {
    return URI.create("/dynamicView");
  }

  @Override
  public View response(Request<Form> request) {
    return new View("dynamicView.html", request.getBody().getValue());
  }

}
