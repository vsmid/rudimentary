package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.security.Authorization;
import java.util.function.Predicate;

public class AuthorizedEndpoint implements HttpEndpoint<Empty, Text> {

    @Override
    public String path() {
        return "/authorized";
    }

    @Override
    public Predicate<Request> authorizations() {
        return Authorization.rolesAllowed("rookie");
    }

    @Override
    public Text response(Request<Empty> request) {
        return new Text("Authorized!");
    }

}
