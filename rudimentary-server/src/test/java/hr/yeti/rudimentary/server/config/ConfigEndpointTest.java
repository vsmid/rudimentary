package hr.yeti.rudimentary.server.config;

import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.server.test.TestServer;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ConfigEndpointTest {

    static TestServer testServer;

    @BeforeAll
    public static void beforeAll() {
        testServer = TestServer.newBuilder()
            .httpEndpoints(ConfigEndpoint.class)
            .build();
        testServer.start();
    }

    @AfterAll
    public static void afterAll() {
        testServer.stop();
    }

    @Test
    public void test_config_response() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("_config");
        HttpRequest GET = HttpRequest.newBuilder(uri).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals(MediaType.TEXT_HTML, response.headers().firstValue("content-type").get());
        assertTrue(response.body().contains("<td>mvc.staticResourcesDir</td>"));
        assertTrue(response.body().contains("<td>mvc.templatesDir</td>"));
        assertTrue(response.body().contains("<td>server.port</td>"));
        assertTrue(response.body().contains("<td>server.stopDelay</td>"));
        assertTrue(response.body().contains("<td>server.threadPoolSize</td>"));
    }
}
