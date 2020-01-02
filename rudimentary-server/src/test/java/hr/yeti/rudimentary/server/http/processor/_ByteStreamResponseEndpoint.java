package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.ByteStream;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class _ByteStreamResponseEndpoint implements HttpEndpoint<ByteStream, ByteStream> {

    @Override
    public URI path() {
        return URI.create("bytestreamresponse");
    }

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public ByteStream response(Request<ByteStream> request) {
        return new ByteStream((outputStream) -> {
            int c;
            while ((c = request.getBody().getValue().read()) != -1) {
                outputStream.write((char) c);
                outputStream.flush();
            }
        });
    }

}
