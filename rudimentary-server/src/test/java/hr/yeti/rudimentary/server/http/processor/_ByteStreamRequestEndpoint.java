package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.ByteStream;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class _ByteStreamRequestEndpoint implements HttpEndpoint<ByteStream, Empty> {

    @Override
    public URI path() {
        return URI.create("bytestreamrequest");
    }

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public Empty response(Request<ByteStream> request) {
        return new Empty();
    }

}
