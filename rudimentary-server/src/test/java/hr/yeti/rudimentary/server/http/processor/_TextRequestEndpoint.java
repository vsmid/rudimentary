package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class _TextRequestEndpoint implements HttpEndpoint<Text, Empty> {

    @Override
    public URI path() {
        return URI.create("textrequest");
    }

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public Empty response(Request<Text> request) {
        return new Empty();
    }

}
