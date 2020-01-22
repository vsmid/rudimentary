package hr.yeti.rudimentary.server.sql;

import hr.yeti.rudimentary.server._DataSources;
import hr.yeti.rudimentary.server._HttpEndpoints;
import hr.yeti.rudimentary.server.test.TestServer;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DataSourceTest {

    static TestServer testServer;

    @BeforeAll
    public static void beforeAll() {
        testServer = TestServer.newBuilder()
            .config(Map.of(
                "dataSource.enabled", "true",
                "dataSource.driverClassName", "org.sqlite.JDBC",
                "dataSource.jdbcUrl", "jdbc:sqlite:file::memory:?cache=shared",
                "dataSource.maximumPoolSize", "5"
            ))
            .dataSources(_DataSources.Ds.class)
            .httpEndpoints(_HttpEndpoints.SqlEndpoint.class)
            .build();
        testServer.start();
    }

    @AfterAll
    public static void afterAll() {
        testServer.stop();
    }

    @Test
    public void test_dataSource_initialized_and_queries_executed() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("sql");
        HttpRequest GET = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals("[{\"ID\":1,\"NAME\":\"Lena\"},{\"ID\":2,\"NAME\":\"Martina\"}]", response.body());
    }

}
