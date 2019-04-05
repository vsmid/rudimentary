package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Form;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class FormEndpoint implements HttpEndpoint<Form, Text> {

  @Override
  public HttpMethod httpMethod() {
    return HttpMethod.POST;
  }

  @Override
  public URI path() {
    return URI.create("/form");
  }

  @Override
  public Text response(Request<Form> request) {
    return new Text(request.getBody().getValue().toString());
  }

}
