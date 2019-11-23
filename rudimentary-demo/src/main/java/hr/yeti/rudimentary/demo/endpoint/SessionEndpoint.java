package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Html;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class SessionEndpoint implements HttpEndpoint<Empty, Html> {

    @Override
    public URI path() {
        return URI.create("/session");
    }

    @Override
    public Html response(Request<Empty> request) {
        return new Html(
                "<html>"
                + "<head></head>"
                + "<body>Hello! Info: " + request.getSession().getAttributes().get("info") + "</body>"
                + "</html>"
        );
    }

    @Override
    public void after(Request<Empty> request, Html response) {
        // Add some data to session after request is processed.
        request.getSession().getAttributes().put("info", "Info for second request");
    }

    @Override
    public String description() {
        return "Demonstrates how to persist data into session.";
    }

}
