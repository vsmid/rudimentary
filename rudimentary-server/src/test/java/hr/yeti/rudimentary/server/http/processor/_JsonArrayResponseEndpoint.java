package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Json;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class _JsonArrayResponseEndpoint implements HttpEndpoint<Empty, Json> {

    @Override
    public URI path() {
        return URI.create("jsonarrayresponse");
    }

    @Override
    public Json response(Request<Empty> request) {
        return new Json(
            javax.json.Json.createArrayBuilder().add("a").build()
        );
    }

}
