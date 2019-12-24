package hr.yeti.rudimentary.demo.endpoint;

import com.sun.net.httpserver.Headers;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.mvc.spi.ViewEndpoint;
import static hr.yeti.rudimentary.validation.Constraint.NOT_NULL;
import hr.yeti.rudimentary.validation.Constraints;
import java.net.URI;
import java.util.Map;

public class CustomViewEndpoint implements ViewEndpoint<Empty> {

    @Override
    public URI path() {
        return URI.create("/dynamicView");
    }

    @Override
    public Constraints constraints(Empty body, Map<String, String> pathVariables, Map<String, String> queryParameters, Headers httpHeaders) {
        return new Constraints() {
            {
                o(queryParameters.get("name"), NOT_NULL);
                o(queryParameters.get("age"), NOT_NULL);
            }
        };
    }

    @Override
    public View response(Request<Empty> request) {
        return new View("dynamicView.html", request.getQueryParameters());
    }

}
