package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.demo.model.OkModel;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Json;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.validation.Constraints;
import java.net.URI;
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
    public Constraints constraints(Request<Json> request) {
        //Manually define constraints for raw JSON array if you do not have a type
        /*new Constraints() {
      {
        request.getBody().getValue()
            .asJsonArray()
            .forEach(
                json -> {
                  o(json.asJsonObject().getString("name"), Constraint.NOT_NULL);
                  o(json.asJsonObject().getString("description"), Constraint.NOT_NULL);
                });
      }
    };*/

        return new Constraints(request.getBody(), OkModel.class);
    }

    @Override
    public Text response(Request<Json> request) {
        List<OkModel> okModels = request.getBody().asListOf(OkModel.class);

        return new Text(
            okModels.stream()
                .map(OkModel::toString)
                .collect(Collectors.joining(System.lineSeparator()))
        );
    }

}
