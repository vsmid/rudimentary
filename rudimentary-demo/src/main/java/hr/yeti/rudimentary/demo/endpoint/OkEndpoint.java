package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.demo.model.OkModel;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.content.Text;
import java.lang.System.Logger.Level;
import java.net.URI;

public class OkEndpoint implements HttpEndpoint<OkModel, Text> {

  ConfigProperty server = new ConfigProperty("server.port", "8888");

  @Override
  public HttpMethod httpMethod() {
    return HttpMethod.POST;
  }

  @Override
  public URI path() {
    return URI.create("/ok/:id");
  }

  @Override
  public int httpStatus() {
    return 201;
  }

  @Override
  public Text response(Request<OkModel> request) {
    logger().log(Level.INFO, Thread.currentThread().getName() + ":: PV ::" + request.getPathVariables().get("id") + ":: QP ::" + request.getQueryParameters().get("name"));

    return new Text("Hello " + request.getBody().orElse(new OkModel()).getName());
  }

}
