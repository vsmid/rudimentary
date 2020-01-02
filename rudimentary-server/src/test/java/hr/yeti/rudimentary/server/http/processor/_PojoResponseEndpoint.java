package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class _PojoResponseEndpoint implements HttpEndpoint<Empty, CarModel> {

    @Override
    public URI path() {
        return URI.create("pojoresponse");
    }

    @Override
    public CarModel response(Request<Empty> request) {
    return new CarModel("Mazda");
    }
    
    

}
