package hr.yeti.rudimentary.server.http.endpoint;

import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.exception.ExceptionInfo;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.ResourceNotFoundException;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.StaticResource;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

// TODO Maybe move to extension?
public class StaticResourceEndpoint implements HttpEndpoint<Empty, StaticResource> {

  private ConfigProperty staticResourcesDir = new ConfigProperty("mvc.staticResourcesDir");

  @Override
  public URI path() {
    return URI.create("/" + staticResourcesDir.value() + "/.*");
  }

  @Override
  public StaticResource response(Request<Empty> request) {
    InputStream staticResource = getClass().getClassLoader().getResourceAsStream(request.getUri().toString().substring(1));

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
  public Optional<String> description() {
    return Optional.of("Serves static files such as javascript and css.");
  }
  
  @Override
  public ExceptionInfo onException(Exception e) {
    return new ExceptionInfo(404, "Could not load resource " + e.getMessage() + ".");
  }

}
