package hr.yeti.rudimentary.server.http.content.handler;

import hr.yeti.rudimentary.server.http.TestHttpExchangeImpl;
import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Json;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

public class JsonContentHandlerTest {

    static ContentHandler<Json> contentHandler;

    @BeforeAll
    public static void beforeAll() {
        contentHandler = new JsonContentHandler();
    }

    @Test
    public void test_read_activate() {
        expect:
        assertTrue(contentHandler.activateReader(JsonEndpoint.class, null));
    }

    @Test
    public void test_write_activate() {
        expect:
        assertTrue(contentHandler.activateWriter(JsonEndpoint.class, null));
    }

    @Test
    public void test_read() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl(new ByteArrayInputStream("{}".getBytes()));
        Json json;

        when:
        json = contentHandler.read(httpExchange, JsonEndpoint.class);

        then:
        assertEquals("{}", json.get().toString());
    }

    @Test
    public void test_write() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        when:
        contentHandler.write(200, new Json(Map.of("name", "Lena")), httpExchange, JsonEndpoint.class);

        then:
        assertEquals(httpExchange.getResponseCode(), 200);
        assertEquals(MediaType.APPLICATION_JSON, httpExchange.getResponseHeaders().get("Content-Type").get(0));
        assertEquals("{\"name\":\"Lena\"}", new String(((ByteArrayOutputStream) httpExchange.getResponseBody()).toByteArray()));
    }

    public static class JsonEndpoint implements HttpEndpoint<Json, Json> {

        @Override
        public Json response(Request<Json> request) {
            return new Json(Map.of());
        }
    }

}
