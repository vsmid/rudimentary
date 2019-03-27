package hr.yeti.rudimentary.demo.endpoint;

import com.sun.net.httpserver.Headers;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Json;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.validation.Constraints;
import java.net.URI;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

public class JsonArrayOkModelEndpoint implements HttpEndpoint<Json, Text> {

  @Override
  public HttpMethod httpMethod() {
    return HttpMethod.POST;
  }

  @Override
  public URI path() {
    return URI.create("/array");
  }

  @Override
  public int httpStatus() {
    return 200;
  }

  @Override
  public Constraints constraints(Json body, Map<String, String> pathVariables, Map<String, String> queryParameters, Headers httpHeaders) {
    //Manually define constraints for raw JSON array if you do not have a type
    /*new Constraints() {
      {
        body.getValue()
            .asJsonArray()
            .forEach(
                json -> {
                  o(json.asJsonObject().getString("name"), Constraint.NOT_NULL);
                  o(json.asJsonObject().getString("description"), Constraint.NOT_NULL);
                });
      }
    };*/

    return new Constraints(body, OkModel.class);
  }

  @Override
  public Text response(Request<Json> request) {
    List<OkModel> okModels = request.getBody().get().asListOf(OkModel.class);

    return new Text(
        okModels.stream()
            .map(OkModel::toString)
            .collect(Collectors.joining(System.lineSeparator()))
    );
  }

}
