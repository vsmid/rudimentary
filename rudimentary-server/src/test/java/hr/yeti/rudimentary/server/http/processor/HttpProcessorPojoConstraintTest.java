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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class HttpProcessorPojoConstraintTest {

    static TestServer testServer;

    @BeforeAll
    public static void beforeAll() {
        testServer = TestServer.newBuilder()
            .httpEndpoints(
                _HttpEndpoints.ConstrainedByHttpEndpointEndpoint.class,
                _HttpEndpoints.ConstrainedByPojoEndpoint.class
            )
            .build();
        testServer.start();
    }

    @AfterAll
    public static void afterAll() {
        testServer.stop();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "{\"name\":\"\"}",
        "{\"name\":\"123\"}"
    })
    public void test_should_report_http_400_due_to_failed_pojo_constraint_validation(String requestBody) throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("pojoconstrainedrequest");
        HttpRequest POST = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(POST, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(400, response.statusCode());
    }

    @Test
    public void test_should_report_http_200_due_to_successful_pojo_constraint_validation() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("pojoconstrainedrequest");
        HttpRequest POST = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"Lena\"}")).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(POST, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
    }
}
