package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.server._HttpEndpoints;
import hr.yeti.rudimentary.server.test.TestServer;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HttpProcessorCustomHttpHeaderInResponseMethodTest {

    static TestServer testServer;

    @BeforeAll
    public static void beforeAll() {
        testServer = TestServer.newBuilder()
            .httpEndpoints(_HttpEndpoints.CustomHttpHeaderInResponseMethodEndpoint.class)
            .build();
        testServer.start();
    }

    @AfterAll
    public static void afterAll() {
        testServer.stop();
    }

    @Test
    public void test_should_set_http_header_by_setting_it_in_response_method() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("customHttpHeaderInResponseMethodEndpoint");
        HttpRequest GET = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals("Lena", response.headers().firstValue("Super-Cute").get());
    }
}
