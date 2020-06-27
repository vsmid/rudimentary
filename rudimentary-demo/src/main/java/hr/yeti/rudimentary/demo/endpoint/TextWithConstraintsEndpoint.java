package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.validation.Constraint;
import hr.yeti.rudimentary.validation.Constraints;

public class TextWithConstraintsEndpoint implements HttpEndpoint<Text, Text> {

    ConfigProperty server = new ConfigProperty("server.port", "8888");

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "/textWithConstraints";
    }

    @Override
    public int httpStatus() {
        return 200;
    }

    @Override
    public Constraints constraints(Request<Text> request) {
        return new Constraints() {
            {
                o(request.getBody().get(), Constraint.NOT_EMPTY);
            }
        };
    }

    @Override
    public Text response(Request<Text> request) {
        return new Text("Hello " + request.getBody().get());
    }

}
