package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.server._HttpEndpoints;
import hr.yeti.rudimentary.server._HttpFilters;
import hr.yeti.rudimentary.server.test.TestServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpProcessorHttpFilterTest {

    static TestServer testServer;

    @BeforeAll
    public static void beforeAll() {
        testServer = TestServer.newBuilder()
            .httpEndpoints(_HttpEndpoints.EmptyRequestEndpoint.class)
            .httpFilters(_HttpFilters.HttpFilter1.class, _HttpFilters.HttpFilter2.class)
            .build();
        testServer.start();
    }

    @AfterAll
    public static void afterAll() {
        testServer.stop();
    }

    @Test
    public void test_httpFilter_order_execution() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("emptyrequest");
        HttpRequest GET = HttpRequest.newBuilder(uri)
            .GET().build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        when:
        System.setOut(new PrintStream(baos));
        HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals("", new String(baos.toByteArray()));
    }

}
