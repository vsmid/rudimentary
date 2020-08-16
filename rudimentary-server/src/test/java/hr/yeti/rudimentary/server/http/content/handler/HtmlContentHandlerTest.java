package hr.yeti.rudimentary.server.http.content.handler;

import hr.yeti.rudimentary.server.http.TestHttpExchangeImpl;
import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Html;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

public class HtmlContentHandlerTest {

    static ContentHandler<Html> contentHandler;

    @BeforeAll
    public static void beforeAll() {
        contentHandler = new HtmlContentHandler();
    }

    @Test
    public void test_read_activate() {
        expect:
        assertTrue(contentHandler.activateReader(HtmlEndpoint.class, null));
    }

    @Test
    public void test_write_activate() {
        expect:
        assertTrue(contentHandler.activateWriter(HtmlEndpoint.class, null));
    }

    @Test
    public void test_read() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl(new ByteArrayInputStream("<h1>ok</h1>".getBytes()));
        Html html;

        when:
        html = contentHandler.read(httpExchange, HtmlEndpoint.class);

        then:
        assertEquals("<h1>ok</h1>", html.get());
    }

    @Test
    public void test_write() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        when:
        contentHandler.write(200, new Html("<h1>ok</h1>"), httpExchange, HtmlEndpoint.class);

        then:
        assertEquals(httpExchange.getResponseCode(), 200);
        assertEquals(MediaType.TEXT_HTML, httpExchange.getResponseHeaders().get("Content-Type").get(0));
        assertEquals(new String(((ByteArrayOutputStream) httpExchange.getResponseBody()).toByteArray()), "<h1>ok</h1>");
    }

    public static class HtmlEndpoint implements HttpEndpoint<Html, Html> {

        @Override
        public Html response(Request<Html> request) {
            return new Html(request.getBody().get());
        }
    }

}
