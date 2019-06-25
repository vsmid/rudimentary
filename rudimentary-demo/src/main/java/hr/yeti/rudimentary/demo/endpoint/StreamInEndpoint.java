package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.ByteStream;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.IOException;
import java.net.URI;

public class StreamInEndpoint implements HttpEndpoint<ByteStream, Text> {

  @Override
  public HttpMethod httpMethod() {
    return HttpMethod.POST;
  }

  @Override
  public URI path() {
    return URI.create("/streamIn");
  }

  @Override
  public Text response(Request<ByteStream> request) {
    byte[] stream = null;

    try {
      stream = request.getBody().getValue().readAllBytes();
    } catch (IOException ex) {
      logger().log(System.Logger.Level.ERROR, ex.getMessage());
    }

    return new Text(new String(stream));
  }

  @Override
  public String description() {
    return "Consuming incoming data stream.";
  }

}
