package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.exception.ExceptionInfo;
import hr.yeti.rudimentary.exception.spi.ExceptionHandler;
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

public class HttpProcessorGlobalExceptionHanderTest {

    static TestServer testServer;

    @BeforeAll
    public static void beforeAll() {
        testServer = TestServer.newBuilder()
            .httpEndpoints(_GlobalExceptionHandlerEndpoint.class)
            .exceptionHandler(MyGlobalExceptionHandler.class)
            .build();
        testServer.start();
    }

    @AfterAll
    public static void afterAll() {
        testServer.stop();
    }

    @Test
    public void test_global_exception_handler() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("globalexceptionhandlerrequest");
        HttpRequest GET = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(999, response.statusCode());
        assertEquals("Damn!", response.body());
    }

    public static class MyGlobalExceptionHandler implements ExceptionHandler {

        @Override
        public ExceptionInfo onException(Exception e) {
            return new ExceptionInfo(999, e.getMessage());
        }

    }

}
