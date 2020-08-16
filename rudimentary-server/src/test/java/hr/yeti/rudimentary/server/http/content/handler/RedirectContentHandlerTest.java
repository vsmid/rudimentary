package hr.yeti.rudimentary.server.http.content.handler;

import hr.yeti.rudimentary.server.http.TestHttpExchangeImpl;
import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Redirect;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

public class RedirectContentHandlerTest {

    static ContentHandler<Redirect> contentHandler;

    @BeforeAll
    public static void beforeAll() {
        contentHandler = new RedirectContentHandler();
    }

    @Test
    public void test_read_activate() {
        expect:
        assertFalse(contentHandler.activateReader(RedirectEndpoint.class, null));
    }

    @Test
    public void test_write_activate() {
        expect:
        assertTrue(contentHandler.activateWriter(RedirectEndpoint.class, null));
    }

    @Test
    public void test_read() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        expect:
        assertThrows(java.lang.UnsupportedOperationException.class, () -> {
            contentHandler.read(httpExchange, RedirectEndpoint.class);
        });
    }

    @Test
    public void test_write() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        when:
        contentHandler.write(
            200,
            new Redirect("http://yeti-it.hr"),
            httpExchange,
            RedirectEndpoint.class
        );

        then:
        assertEquals(httpExchange.getResponseCode(), 302);
        assertEquals("http://yeti-it.hr", httpExchange.getResponseHeaders().get("Location").get(0));
        assertNull(httpExchange.getResponseBody());
    }

    public static class RedirectEndpoint implements HttpEndpoint<Empty, Redirect> {

        @Override
        public Redirect response(Request<Empty> request) {
            return null;
        }

    }

}
