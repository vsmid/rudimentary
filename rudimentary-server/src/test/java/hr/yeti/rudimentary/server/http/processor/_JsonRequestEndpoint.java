package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Json;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class _JsonRequestEndpoint implements HttpEndpoint<Json, Empty> {

    @Override
    public URI path() {
        return URI.create("jsonrequest");
    }

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public Empty response(Request<Json> request) {
        return new Empty();
    }

}
