package hr.yeti.rudimentary.server.http.content.handler;

import hr.yeti.rudimentary.server.http.TestHttpExchangeImpl;
import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.mvc.spi.ViewEndpoint;
import hr.yeti.rudimentary.server.mvc.DefaultStaticHTMLViewEngine;
import hr.yeti.rudimentary.test.ContextMock;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

public class ViewContentHandlerTest {

    static ContentHandler<View> contentHandler;

    @BeforeAll
    public static void beforeAll() {
        contentHandler = new ViewContentHandler();
    }

    @Test
    public void test_read_activate() {
        expect:
        assertFalse(contentHandler.activateReader(CustomViewEndpoint.class, null));
    }

    @Test
    public void test_write_activate() {
        expect:
        assertTrue(contentHandler.activateWriter(CustomViewEndpoint.class, null));
    }

    @Test
    public void test_read() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        expect:
        assertThrows(java.lang.UnsupportedOperationException.class, () -> {
            contentHandler.read(httpExchange, CustomViewEndpoint.class);
        });
    }

    @Test
    public void test_write() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        ContextMock contextMock = new ContextMock(Map.of("mvc.templatesDir", "view"), ViewContentHandler.class, DefaultStaticHTMLViewEngine.class);

        when:
        Instance.of(ViewContentHandler.class).write(200, new View("yeti.html"), httpExchange, CustomViewEndpoint.class);

        then:
        assertEquals(httpExchange.getResponseCode(), 200);
        assertEquals(MediaType.TEXT_HTML, httpExchange.getResponseHeaders().get("Content-Type").get(0));
        assertTrue(
            new String(((ByteArrayOutputStream) httpExchange.getResponseBody()).toByteArray()).contains("<div>Rudimentary framework.</div>")
        );
    }

    public static class CustomViewEndpoint implements ViewEndpoint<Empty> {

        @Override
        public View response(Request<Empty> request) {
            return new View("yeti.html");
        }

    }

}
