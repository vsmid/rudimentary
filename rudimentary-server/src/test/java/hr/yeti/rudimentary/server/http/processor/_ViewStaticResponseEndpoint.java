package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.mvc.spi.ViewEndpoint;
import java.net.URI;

public class _ViewStaticResponseEndpoint implements ViewEndpoint<Empty> {

    @Override
    public URI path() {
        return URI.create("staticviewresponse");
    }

    @Override
    public View response(Request<Empty> request) {
        return new View("yeti.html", null);
    }

}
