package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Redirect;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class _RedirectResponseEndpoint implements HttpEndpoint<Empty, Redirect> {

    @Override
    public URI path() {
        return URI.create("redirectresponse");
    }

    @Override
    public Redirect response(Request<Empty> request) {
        return new Redirect(URI.create("redirected"), 302);
    }

}
