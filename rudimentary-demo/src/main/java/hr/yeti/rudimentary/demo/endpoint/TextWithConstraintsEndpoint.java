package hr.yeti.rudimentary.demo.endpoint;

import com.sun.net.httpserver.Headers;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.validation.Constraint;
import hr.yeti.rudimentary.validation.Constraints;
import java.net.URI;
import java.util.Map;

public class TextWithConstraintsEndpoint implements HttpEndpoint<Text, Text> {

    ConfigProperty server = new ConfigProperty("server.port", "8888");

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public URI path() {
        return URI.create("/textWithConstraints");
    }

    @Override
    public int httpStatus() {
        return 200;
    }

    @Override
    public Constraints constraints(
        Text body,
        Map<String, String> pathVariables,
        Map<String, String> queryParameters,
        Headers httpHeaders
    ) {
        return new Constraints() {
            {
                o(body.getValue(), Constraint.NOT_EMPTY);
            }
        };
    }

    @Override
    public Text response(Request<Text> request) {
        return new Text("Hello " + request.getBody().getValue());
    }

}
