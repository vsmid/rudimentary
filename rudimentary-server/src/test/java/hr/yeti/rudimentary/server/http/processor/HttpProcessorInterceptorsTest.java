package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.server._HttpEndpoints;
import hr.yeti.rudimentary.server._Interceptors;
import hr.yeti.rudimentary.server.test.TestServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class HttpProcessorInterceptorsTest {

    @Test
    public void test_before_interceptor() throws IOException, InterruptedException {
        // setup:
        TestServer testServer = TestServer.newBuilder()
            .beforeInterceptors(_Interceptors.BeforeInterceptor1.class)
            .httpEndpoints(_HttpEndpoints.EmptyRequestEndpoint.class)
            .build();
        testServer.start();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        URI uri = testServer.buildUri("emptyrequest");
        HttpRequest GET = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals("1", new String(baos.toByteArray()));

        testServer.stop();
    }

    @Test
    public void test_before_order_interceptor() throws IOException, InterruptedException {
        // setup:
        TestServer testServer = TestServer.newBuilder()
            .beforeInterceptors(_Interceptors.BeforeInterceptor1.class, _Interceptors.BeforeInterceptor2.class)
            .httpEndpoints(_HttpEndpoints.EmptyRequestEndpoint.class)
            .build();
        testServer.start();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        URI uri = testServer.buildUri("emptyrequest");
        HttpRequest GET = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals("12", new String(baos.toByteArray()));

        testServer.stop();
    }

    @Test
    public void test_after_interceptor() throws IOException, InterruptedException {
        // setup:
        TestServer testServer = TestServer.newBuilder()
            .afterInterceptors(_Interceptors.AfterInterceptor1.class)
            .httpEndpoints(_HttpEndpoints.EmptyRequestEndpoint.class)
            .build();
        testServer.start();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        URI uri = testServer.buildUri("emptyrequest");
        HttpRequest GET = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals("3", new String(baos.toByteArray()));

        testServer.stop();
    }

    @Test
    public void test_after_order_interceptor() throws IOException, InterruptedException {
        // setup:
        TestServer testServer = TestServer.newBuilder()
            .afterInterceptors(_Interceptors.AfterInterceptor1.class, _Interceptors.AfterInterceptor2.class)
            .httpEndpoints(_HttpEndpoints.EmptyRequestEndpoint.class)
            .build();
        testServer.start();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        URI uri = testServer.buildUri("emptyrequest");
        HttpRequest GET = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals("34", new String(baos.toByteArray()));

        testServer.stop();
    }

    @Test
    public void test_before_for_uri_interceptor() throws IOException, InterruptedException {
        // setup:
        TestServer testServer = TestServer.newBuilder()
            .beforeInterceptors(
                _Interceptors.BeforeInterceptor1.class,
                _Interceptors.BeforeInterceptorForURI.class
            )
            .httpEndpoints(
                _HttpEndpoints.EmptyRequestEndpoint.class,
                _HttpEndpoints.BeforeInterceptorForURIEndpoint.class
            )
            .build();
        testServer.start();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        URI uri = testServer.buildUri("emptyrequest");
        HttpRequest GET = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals("1", new String(baos.toByteArray()));

        and:

        when:
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        uri = testServer.buildUri("uriinterceptbefore");
        GET = HttpRequest.newBuilder(uri).GET().build();
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals("1before", new String(baos.toByteArray()));

        testServer.stop();
    }

    @Test
    public void test_after_for_uri_interceptor() throws IOException, InterruptedException {
        // setup:
        TestServer testServer = TestServer.newBuilder()
            .afterInterceptors(
                _Interceptors.AfterInterceptor1.class,
                _Interceptors.AfterInterceptorForURI.class
            )
            .httpEndpoints(
                _HttpEndpoints.EmptyRequestEndpoint.class,
                _HttpEndpoints.AfterInterceptorForURIEndpoint.class
            )
            .build();
        testServer.start();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        URI uri = testServer.buildUri("emptyrequest");
        HttpRequest GET = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals("3", new String(baos.toByteArray()));

        and:

        when:
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        uri = testServer.buildUri("uriinterceptafter");
        GET = HttpRequest.newBuilder(uri).GET().build();
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals("3after", new String(baos.toByteArray()));

        testServer.stop();
    }

    @Test
    public void test_httpEndpoint_before_executes_after_global_before_interceptor() throws IOException, InterruptedException {
        // setup:
        TestServer testServer = TestServer.newBuilder()
            .beforeInterceptors(_Interceptors.BeforeInterceptor1.class)
            .httpEndpoints(_HttpEndpoints.HttpEndpointWithBeforeInterceptEndpoint.class)
            .build();
        testServer.start();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        URI uri = testServer.buildUri("uriinterceptwithbefore");
        HttpRequest GET = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals("12", new String(baos.toByteArray()));

        testServer.stop();
    }

    @Test
    public void test_httpEndpoint_after_executes_before_global_after_interceptor() throws IOException, InterruptedException {
        // setup:
        TestServer testServer = TestServer.newBuilder()
            .afterInterceptors(_Interceptors.AfterInterceptorForURI.class)
            .httpEndpoints(_HttpEndpoints.HttpEndpointWithAfterInterceptEndpoint.class)
            .build();
        testServer.start();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        URI uri = testServer.buildUri("uriinterceptwithafter");
        HttpRequest GET = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals("2after", new String(baos.toByteArray()));

        testServer.stop();
    }
}
