package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.MediaType;
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

public class HttpProcessorRequestContentTypeTest {

    static TestServer testServer;

    @BeforeAll
    public static void beforeAll() {
        testServer = TestServer.newBuilder()
            .httpEndpoints(
                _EmptyRequestEndpoint.class,
                _TextRequestEndpoint.class,
                _FormRequestEndpoint.class,
                _JsonRequestEndpoint.class,
                _ByteStreamRequestEndpoint.class,
                _HtmlRequestEndpoint.class,
                _PojoRequestEndpoint.class
            )
            .build();
        testServer.start();
    }

    @AfterAll
    public static void afterAll() {
        testServer.stop();
    }

    @Test
    public void test_should_receive_empty_body() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("emptyrequest");
        HttpRequest GET = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
    }

    @Test
    public void test_should_receive_text_body() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("textrequest");
        HttpRequest POST = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString("text")).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(POST, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
    }

    @Test
    public void test_should_receive_form_body() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("formrequest");
        HttpRequest POST = HttpRequest.newBuilder(uri)
            .POST(HttpRequest.BodyPublishers.ofString("name=Lena&age=0"))
            .header("Content-type", MediaType.APPLICATION_FORM_URLENCODED).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(POST, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
    }

    @Test
    public void test_should_receive_json_body() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("jsonrequest");
        HttpRequest POST = HttpRequest.newBuilder(uri)
            .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"lena\", \"age\":0}"))
            .build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(POST, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
    }

    @Test
    public void test_should_receive_bytestream_body() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("bytestreamrequest");
        HttpRequest POST = HttpRequest.newBuilder(uri)
            .POST(HttpRequest.BodyPublishers.ofInputStream(() -> {
                return getClass().getResourceAsStream("/streamcontent.txt");
            }))
            .build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(POST, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
    }

    @Test
    public void test_should_receive_html_body() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("htmlrequest");
        HttpRequest POST = HttpRequest.newBuilder(uri)
            .POST(HttpRequest.BodyPublishers.ofString("<html></html>"))
            .build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(POST, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
    }
    
    @Test
    public void test_should_convert_json_body_to_pojo() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("pojorequest");
        HttpRequest POST = HttpRequest.newBuilder(uri)
            .POST(HttpRequest.BodyPublishers.ofString("{\"manufacturer\":\"Mazda\"}"))
            .build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(POST, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
    }

}
