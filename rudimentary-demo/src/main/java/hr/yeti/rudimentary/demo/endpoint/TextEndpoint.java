package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.content.Text;
import java.net.URI;

public class TextEndpoint implements HttpEndpoint<Text, Text> {

  ConfigProperty server = new ConfigProperty("server.port", "8888");

  @Override
  public HttpMethod httpMethod() {
    return HttpMethod.POST;
  }

  @Override
  public URI path() {
    return URI.create("/text");
  }

  @Override
  public int httpStatus() {
    return 200;
  }

  @Override
  public Text response(Request<Text> request) {
    return new Text("Hello " + request.getBody().get().getValue());
  }

}
