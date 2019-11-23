package hr.yeti.rudimentary.demo.endpoint;

import com.sun.net.httpserver.Headers;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.content.Json;
import hr.yeti.rudimentary.validation.Constraint;
import hr.yeti.rudimentary.validation.Constraints;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.util.Map;

public class AnyJsonEndpoint implements HttpEndpoint<Json, Text> {

    ConfigProperty server = new ConfigProperty("server.port", "8888");

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public URI path() {
        return URI.create("/json");
    }

    @Override
    public int httpStatus() {
        return 201;
    }

    @Override
    public Constraints constraints(
            Json body,
            Map<String, String> pathVariables,
            Map<String, String> queryParameters,
            Headers httpHeaders) {

        // If you would have an exact type to map json to e.g. OkModel you would simply
        // use new Constraints(body, OkModel.class);
        return new Constraints() {
            {
                o(body.getValue().asJsonObject().getString("name"), Constraint.NOT_NULL);
            }
        };
    }

    @Override
    public Text response(Request<Json> request) {
        logger().log(Level.INFO, Thread.currentThread().getName() + ":: PV ::" + request.getPathVariables().get("id") + ":: QP ::" + request.getQueryParameters().get("name"));

        return new Text("Hello " + request.getBody().toString());
    }

    @Override
    public String description() {
        return "Endpoint which receives any json in request body.";
    }

}
