package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Html;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;

public class HtmlEndpoint implements HttpEndpoint<Empty, Html> {

    @Override
    public String path() {
        return "/html";
    }

    @Override
    public Html response(Request<Empty> request) {
        return new Html(
            "<html>"
            + "<head></head>"
            + "<body>Hello!</body>"
            + "</html>"
        );
    }

}
