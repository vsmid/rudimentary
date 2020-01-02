package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Json;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;
import java.util.Map;

public class _JsonResponseEndpoint implements HttpEndpoint<Empty, Json> {

    @Override
    public URI path() {
        return URI.create("jsonresponse");
    }

    @Override
    public Json response(Request<Empty> request) {
        return new Json(
            Map.of("name", "lena")
        );
    }

}
