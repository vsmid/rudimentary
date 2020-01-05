package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class _PojoResponseEndpoint implements HttpEndpoint<Empty, Models._CarModel> {

    @Override
    public URI path() {
        return URI.create("pojoresponse");
    }

    @Override
    public Models._CarModel response(Request<Empty> request) {
        return new Models._CarModel("Mazda");
    }

}
