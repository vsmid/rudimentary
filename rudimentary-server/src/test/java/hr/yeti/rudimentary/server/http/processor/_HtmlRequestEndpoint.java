package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Html;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class _HtmlRequestEndpoint implements HttpEndpoint<Html, Empty> {

    @Override
    public URI path() {
        return URI.create("htmlrequest");
    }

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public Empty response(Request<Html> request) {
        return new Empty();
    }

}
