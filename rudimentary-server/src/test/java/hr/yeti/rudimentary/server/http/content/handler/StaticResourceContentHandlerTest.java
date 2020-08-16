package hr.yeti.rudimentary.server.http.content.handler;

import hr.yeti.rudimentary.server.http.TestHttpExchangeImpl;
import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.StaticResource;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

public class StaticResourceContentHandlerTest {

    static ContentHandler<StaticResource> contentHandler;

    @BeforeAll
    public static void beforeAll() {
        contentHandler = new StaticResourceContentHandler();
    }

    @Test
    public void test_read_activate() {
        expect:
        assertFalse(contentHandler.activateReader(StaticResourceEndpoint.class, null));
    }

    @Test
    public void test_write_activate() {
        expect:
        assertTrue(contentHandler.activateWriter(StaticResourceEndpoint.class, null));
    }

    @Test
    public void test_read() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        expect:
        assertThrows(java.lang.UnsupportedOperationException.class, () -> {
            contentHandler.read(httpExchange, StaticResourceEndpoint.class);
        });
    }

    @Test
    public void test_write() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        when:
        contentHandler.write(
            200,
            new StaticResource(new ByteArrayInputStream("var i = 0;".getBytes()), MediaType.APPLICATION_JAVASCRIPT),
            httpExchange, StaticResourceEndpoint.class
        );

        then:
        assertEquals(httpExchange.getResponseCode(), 200);
        assertEquals(MediaType.APPLICATION_JAVASCRIPT, httpExchange.getResponseHeaders().get("Content-Type").get(0));
        assertTrue(
            new String(((ByteArrayOutputStream) httpExchange.getResponseBody()).toByteArray()).contains("var i = 0")
        );
    }

    public static class StaticResourceEndpoint implements HttpEndpoint<Empty, StaticResource> {

        @Override
        public StaticResource response(Request<Empty> request) {
            return null;
        }

    }

}
