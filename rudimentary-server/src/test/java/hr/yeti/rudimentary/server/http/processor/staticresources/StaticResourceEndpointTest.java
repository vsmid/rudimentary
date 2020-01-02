package hr.yeti.rudimentary.server.http.processor.staticresources;

import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.server.http.staticresources.StaticResourcesEndpoint;
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

public class StaticResourceEndpointTest {

    static TestServer testServer;

    @BeforeAll
    public static void beforeAll() {
        testServer = TestServer.newBuilder()
            .httpEndpoints(StaticResourcesEndpoint.class)
            .build();
        testServer.start();
    }

    @AfterAll
    public static void afterAll() {
        testServer.stop();
    }

    @Test
    public void test_staticresource_javascript_response() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("static/yeti.js");
        HttpRequest GET = HttpRequest.newBuilder(uri).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals(MediaType.APPLICATION_JAVASCRIPT, response.headers().firstValue("content-type").get());
    }

    @Test
    public void test_staticresource_css_response() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("static/yeti.css");
        HttpRequest GET = HttpRequest.newBuilder(uri).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals(MediaType.TEXT_CSS, response.headers().firstValue("content-type").get());
    }
    
    @Test
    public void test_staticresource_image_response() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("static/yeti.png");
        HttpRequest GET = HttpRequest.newBuilder(uri).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.headers().firstValue("content-type").get());
    }
}
