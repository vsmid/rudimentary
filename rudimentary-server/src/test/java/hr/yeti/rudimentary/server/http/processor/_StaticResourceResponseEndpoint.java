package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.StaticResource;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.server.resources.ClasspathResource;
import java.net.URI;

public class _StaticResourceResponseEndpoint implements HttpEndpoint<Empty, StaticResource> {

    @Override
    public URI path() {
        return URI.create("staticresourceresponse");
    }

    @Override
    public StaticResource response(Request<Empty> request) {
        return new StaticResource(new ClasspathResource("static/yeti.js").get(), MediaType.APPLICATION_JAVASCRIPT);
    }

}
