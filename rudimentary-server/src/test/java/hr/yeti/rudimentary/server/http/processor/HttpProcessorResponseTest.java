package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.server.test.TestServer;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HttpProcessorResponseTest {

    static TestServer testServer;

    @BeforeAll
    public static void beforeAll() {
        testServer = TestServer.newBuilder()
            .httpEndpoints(
                _ByteStreamResponseEndpoint.class,
                _EmptyResponseEndpoint.class,
                _TextResponseEndpoint.class,
                _HtmlResponseEndpoint.class,
                _JsonResponseEndpoint.class,
                _JsonArrayResponseEndpoint.class,
                _RedirectResponseEndpoint.class,
                _PojoResponseEndpoint.class,
                _StaticResourceResponseEndpoint.class
            )
            .viewEndpoints(_ViewStaticResponseEndpoint.class)
            .build();
        testServer.start();
    }

    @AfterAll
    public static void afterAll() {
        testServer.stop();
    }

    @Test
    public void test_bytestream_response() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("bytestreamresponse");
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
        assertEquals("chunked", response.headers().firstValue("transfer-encoding").get());
        assertEquals("dummy", response.body());
    }

    @Test
    public void test_empty_response() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("emptyresponse");
        HttpRequest GET = HttpRequest.newBuilder(uri)
            .GET().build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        response.headers();
        assertEquals(200, response.statusCode());
        assertEquals(MediaType.ALL, response.headers().firstValue("content-type").get());
        assertEquals("", response.body());
    }

    @Test
    public void test_text_response() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("textresponse");
        HttpRequest GET = HttpRequest.newBuilder(uri).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals(MediaType.TEXT_PLAIN, response.headers().firstValue("content-type").get());
        assertEquals("hello world.", response.body());
    }

    @Test
    public void test_html_response() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("htmlresponse");
        HttpRequest GET = HttpRequest.newBuilder(uri).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals(MediaType.TEXT_HTML, response.headers().firstValue("content-type").get());
        assertEquals("<html></html>", response.body());
    }

    @Test
    public void test_json_response() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("jsonresponse");
        HttpRequest GET = HttpRequest.newBuilder(uri).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.headers().firstValue("content-type").get());
        assertEquals("{\"name\":\"lena\"}", response.body());
    }

    @Test
    public void test_jsonarray_response() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("jsonarrayresponse");
        HttpRequest GET = HttpRequest.newBuilder(uri).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.headers().firstValue("content-type").get());
        assertEquals("[\"a\"]", response.body());
    }

    @Test
    public void test_redirect_response() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("redirectresponse");
        HttpRequest GET = HttpRequest.newBuilder(uri).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(302, response.statusCode());
        assertEquals("redirected", response.headers().firstValue("location").get());
    }

    @Test
    public void test_staticresource_javascript_response() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("staticresourceresponse");
        HttpRequest GET = HttpRequest.newBuilder(uri).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals(MediaType.APPLICATION_JAVASCRIPT, response.headers().firstValue("content-type").get());
    }

    @Test
    public void test_static_view_response() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("staticviewresponse");
        HttpRequest GET = HttpRequest.newBuilder(uri).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals(MediaType.TEXT_HTML, response.headers().firstValue("content-type").get());
        Assertions.assertTrue(response.body().contains("<div>Rudimentary framework.</div>"));
    }

    @Test
    public void test_should_convert_pojo_response_to_json() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("pojoresponse");
        HttpRequest GET = HttpRequest.newBuilder(uri).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals("{\"manufacturer\":\"Mazda\"}", response.body());
    }
}
