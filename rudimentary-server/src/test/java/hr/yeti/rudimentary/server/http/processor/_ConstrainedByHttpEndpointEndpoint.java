package hr.yeti.rudimentary.server.http.processor;

import com.sun.net.httpserver.Headers;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.validation.Constraint;
import hr.yeti.rudimentary.validation.Constraints;
import java.net.URI;
import java.util.Map;
import java.util.regex.Pattern;

public class _ConstrainedByHttpEndpointEndpoint implements HttpEndpoint<Text, Empty> {

    final Pattern onlyLetters = Pattern.compile("[a-zA-Z]+");

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public URI path() {
        return URI.create("constrainedrequest");
    }

    @Override
    public Constraints constraints(Text body, Map<String, String> pathVariables, Map<String, String> queryParameters, Headers httpHeaders) {
        return new Constraints() {
            {
                o(body.getValue(), Constraint.NOT_EMPTY, Constraint.REGEX(onlyLetters));
            }
        };
    }

    @Override
    public Empty response(Request<Text> request) {
        return new Empty();
    }

}
