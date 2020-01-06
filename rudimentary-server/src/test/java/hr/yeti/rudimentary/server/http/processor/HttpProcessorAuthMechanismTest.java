package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.server._HttpEndpoints;
import hr.yeti.rudimentary.server.security.auth.basic.BasicAuthMechanism;
import hr.yeti.rudimentary.server.security.identitystore.embedded.EmbeddedIdentityStore;
import hr.yeti.rudimentary.server.test.TestServer;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HttpProcessorAuthMechanismTest {

    static TestServer testServer;

    @BeforeAll
    public static void beforeAll() {
        testServer = TestServer.newBuilder()
            .config(
                Map.of(
                    "security.basic.enabled", "true",
                    "security.realm", "demo",
                    "security.identityStore.embedded.enabled", "true",
                    "security.identityStore.embedded.identities", "vsmid:pass:admins:rookie:email=vsmid@gmail.com,city=Zagreb;mmeglic:pass::read,write;",
                    "security.urisRequiringAuthentication", "auth",
                    "security.urisNotRequiringAuthentication", ""
                )
            )
            .authMechanism(BasicAuthMechanism.class, EmbeddedIdentityStore.class, null)
            .httpEndpoints(_HttpEndpoints.AuthMechanismEndpoint.class)
            .build();
        testServer.start();
    }

    @AfterAll
    public static void afterAll() {
        testServer.stop();
    }

    @Test
    public void test_successful_basic_auth() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("auth");
        HttpRequest GET = HttpRequest.newBuilder(uri).GET().header("Authorization", "Basic " + Base64.getEncoder().encodeToString("vsmid:pass".getBytes())).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals("Identity{username=vsmid, realm=demo, groups=[admins], roles=[rookie], details={city=Zagreb, email=vsmid@gmail.com}}", response.body());
    }
    
    @Test
    public void test_failed_basic_auth() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("auth");
        HttpRequest GET = HttpRequest.newBuilder(uri).GET().header("Authorization", "Basic " + Base64.getEncoder().encodeToString("vsmid:pass123".getBytes())).build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(401, response.statusCode());
    }

}
