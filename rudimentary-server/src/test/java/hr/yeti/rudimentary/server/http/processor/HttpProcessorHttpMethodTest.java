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

public class HttpProcessorHttpMethodTest {

    static TestServer testServer;

    @BeforeAll
    public static void beforeAll() {
        testServer = TestServer.newBuilder()
            .httpEndpoints(_HttpEndpoints.EmptyRequestEndpoint.class)
            .build();
        testServer.start();
    }

    @AfterAll
    public static void afterAll() {
        testServer.stop();
    }
    
    @Test
    public void test_report_http_status_405() throws IOException, InterruptedException {
         // setup:
        URI uri = testServer.buildUri("emptyrequest");
        HttpRequest POST = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(POST, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(405, response.statusCode());
    }

}
