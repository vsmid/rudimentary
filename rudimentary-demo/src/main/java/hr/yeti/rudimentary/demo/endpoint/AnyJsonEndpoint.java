package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.content.Json;
import hr.yeti.rudimentary.validation.Constraint;
import hr.yeti.rudimentary.validation.Constraints;
import javax.json.JsonValue;

public class AnyJsonEndpoint implements HttpEndpoint<Json, Text> {

    ConfigProperty server = new ConfigProperty("server.port", "8888");

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "/json";
    }

    @Override
    public int httpStatus() {
        return 201;
    }

    @Override
    public Constraints constraints(Request<Json> request) {
        // If you would have an exact type to map json to e.g. OkModel you would simply
        // use new Constraints(body, OkModel.class);
        return new Constraints() {
            {
                o(request.getBody().get().asJsonObject().getString("name", null), Constraint.NOT_NULL);
            }
        };
    }

    @Override
    public Text response(Request<Json> request) {
        return new Text("Hello " + request.getBody().as(JsonValue.class));
    }

    @Override
    public String description() {
        return "Endpoint which receives any json in request body.";
    }

}
