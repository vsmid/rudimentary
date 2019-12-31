package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.server.test.TestServer;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class HttpProcessorTest {

  @Test
  public void test_empty_body() throws IOException, InterruptedException {
    // setup:
    TestServer testServer = TestServer.newBuilder()
        .httpEndpoints(EmptyBodyEndpoint.class)
        .build();
    URI uri;

    when:
    testServer.start();
    uri = testServer.buildUri("emptybody");

    HttpRequest GET = HttpRequest.newBuilder(uri).GET().build();
    HttpResponse<String> response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

    then:
    assertEquals(200, response.statusCode());
    assertEquals("", response.body());
    testServer.stop();
  }

}
