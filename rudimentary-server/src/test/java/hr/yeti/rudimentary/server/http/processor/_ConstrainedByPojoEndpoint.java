package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class _ConstrainedByPojoEndpoint implements HttpEndpoint<_PojoConstrained, Empty> {

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public URI path() {
        return URI.create("pojoconstrainedrequest");
    }

    @Override
    public Empty response(Request<_PojoConstrained> request) {
        return new Empty();
    }

}
