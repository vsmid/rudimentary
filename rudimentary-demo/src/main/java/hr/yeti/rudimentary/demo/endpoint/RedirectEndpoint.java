package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Redirect;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;

public class RedirectEndpoint implements HttpEndpoint<Empty, Redirect> {

    @Override
    public Redirect response(Request<Empty> request) {
        return new Redirect("http://www.yeti-it.hr");
    }
}
