package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Html;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class _HtmlResponseEndpoint implements HttpEndpoint<Empty, Html> {

    @Override
    public URI path() {
        return URI.create("htmlresponse");
    }

    @Override
    public Html response(Request<Empty> request) {
        return new Html("<html></html>");
    }

}
