package hr.yeti.rudimentary.server.http.staticresources;

import com.sun.net.httpserver.Headers;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.exception.ExceptionInfo;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.ResourceNotFoundException;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.StaticResource;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.server.resources.ClasspathResource;
import java.io.InputStream;
import java.util.Objects;

public class StaticResourcesEndpoint implements HttpEndpoint<Empty, StaticResource> {

    private ConfigProperty staticResourcesDir = new ConfigProperty("mvc.staticResourcesDir");

    @Override
    public String path() {
        return staticResourcesDir.value() + "/.*";
    }

    @Override
    public StaticResource response(Request<Empty> request) {
        InputStream staticResource = new ClasspathResource(request.getUri().toString()).get();

        if (Objects.isNull(staticResource)) {
            throw new ResourceNotFoundException(request.getUri().toString());
        }

        int extensionIndex = request.getUri().toString().lastIndexOf(".");
        String extension = request.getUri().toString().substring(extensionIndex + 1).toLowerCase();

        String mediaType = MediaType.APPLICATION_OCTET_STREAM;

        switch (extension) {
            case "js":
                mediaType = MediaType.APPLICATION_JAVASCRIPT;
                break;
            case "css":
                mediaType = MediaType.TEXT_CSS;
                break;
            default:
                break;
        }

        return new StaticResource(staticResource, mediaType);
    }

    @Override
    public String description() {
        return "Serves static files such as javascript and css.";
    }

    @Override
    public ExceptionInfo onException(Exception e, Headers responseHttpHeaders) {
        return new ExceptionInfo(404, ("Could not load resource " + e.getMessage() + ".").getBytes());
    }

}
