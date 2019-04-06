package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.demo.model.BlogPost;
import hr.yeti.rudimentary.events.EventPublisher;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;
import java.util.Optional;

public class EventEndpoint implements HttpEndpoint<BlogPost, Text> {

  @Override
  public HttpMethod httpMethod() {
    return HttpMethod.POST;
  }

  @Override
  public URI path() {
    return URI.create("/blogpost");
  }

  @Override
  public int httpStatus() {
    return 201;
  }

  @Override
  public Text response(Request<BlogPost> request) {
    // Imagine saving blog post to database
    // Publish received blogpost as event
    request.getBody().publish(EventPublisher.Type.SYNC);

    return new Text(request.getBody().getText());
  }

  @Override
  public String description() {
    return "Receives blog post and chnages its content through event listener.";
  }
}
