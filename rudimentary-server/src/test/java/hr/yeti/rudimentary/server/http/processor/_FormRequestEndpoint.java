package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Form;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class _FormRequestEndpoint implements HttpEndpoint<Form, Empty> {

    @Override
    public URI path() {
        return URI.create("formrequest");
    }

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public Empty response(Request<Form> request) {
        return new Empty();
    }

}
