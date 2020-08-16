package hr.yeti.rudimentary.server.http.content.handler;

import hr.yeti.rudimentary.server.http.TestHttpExchangeImpl;
import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

public class EmptyContentHandlerTest {

    static ContentHandler<Empty> contentHandler;

    @BeforeAll
    public static void beforeAll() {
        contentHandler = new EmptyContentHandler();
    }

    @Test
    public void test_read_activate() {
        expect:
        assertTrue(contentHandler.activateReader(EmptyEndpoint.class, null));
    }

    @Test
    public void test_write_activate() {
        expect:
        assertTrue(contentHandler.activateWriter(EmptyEndpoint.class, null));
    }

    @Test
    public void test_read() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl(new ByteArrayInputStream("ok".getBytes()));
        Empty empty;

        when:
        empty = contentHandler.read(httpExchange, EmptyEndpoint.class);

        then:
        assertEquals(Empty.INSTANCE, empty);
    }

    @Test
    public void test_write() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        when:
        contentHandler.write(200, Empty.INSTANCE, httpExchange, EmptyEndpoint.class);

        then:
        assertEquals(httpExchange.getResponseCode(), 200);
        assertEquals(MediaType.ALL, httpExchange.getResponseHeaders().get("Content-Type").get(0));
        assertNull(httpExchange.getResponseBody());
    }

    public static class EmptyEndpoint implements HttpEndpoint<Empty, Empty> {

        @Override
        public Empty response(Request<Empty> request) {
            return Empty.INSTANCE;
        }
    }

}
