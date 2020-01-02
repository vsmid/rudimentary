package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class _TextResponseEndpoint implements HttpEndpoint<Empty, Text> {

    @Override
    public URI path() {
        return URI.create("textresponse");
    }

    @Override
    public Text response(Request<Empty> request) {
        return new Text("hello world.");
    }

}
