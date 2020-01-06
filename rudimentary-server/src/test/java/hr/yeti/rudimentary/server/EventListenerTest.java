package hr.yeti.rudimentary.server;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.server.test.TestServer;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EventListenerTest {

    static TestServer testServer;

    @BeforeAll
    public static void beforeAll() {
        testServer = TestServer.newBuilder()
            .httpEndpoints(_HttpEndpoints.BlogPostEventPublishingEndpoint.class)
            .eventListeners(_EventListeners.BlogPostListener.class)
            .build();
        testServer.start();
    }

    @AfterAll
    public static void afterAll() {
        testServer.stop();
    }

    @Test
    public void test_sync_publish_and_process_event() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("blogpost" + URLEncoder.encode("?type=sync", StandardCharsets.UTF_8));
        HttpRequest POST = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString("{\"text\":\"Hello World.\"}")).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(POST, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals("HELLO WORLD.", Instance.of(_HttpEndpoints.BlogPostEventPublishingEndpoint.class).text);
        assertEquals("HELLO WORLD.", response.body());
    }

    @Test
    public void test_async_publish_and_process_event() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("blogpost" + URLEncoder.encode("?type=async", StandardCharsets.UTF_8));
        HttpRequest POST = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString("{\"text\":\"Hello World.\"}")).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(POST, HttpResponse.BodyHandlers.ofString());
        Instance.of(_HttpEndpoints.BlogPostEventPublishingEndpoint.class);

        then:
        assertEquals(200, response.statusCode());
        assertEquals("Hello World.", Instance.of(_HttpEndpoints.BlogPostEventPublishingEndpoint.class).text);
        assertEquals("Hello World.", response.body());

        and:

        when:
        TimeUnit.MILLISECONDS.sleep(30);
        assertEquals("HELLO WORLD.", Instance.of(_HttpEndpoints.BlogPostEventPublishingEndpoint.class).text);
    }

}
