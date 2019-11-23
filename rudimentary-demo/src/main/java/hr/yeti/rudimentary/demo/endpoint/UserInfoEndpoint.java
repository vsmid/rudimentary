package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;

public class UserInfoEndpoint implements HttpEndpoint<Empty, Text> {

    @Override
    public Text response(Request<Empty> request) {
        return new Text(request.getIdentity().toString());
    }

}
