package hr.yeti.rudimentary.server.http.content.handler;

import hr.yeti.rudimentary.server.http.TestHttpExchangeImpl;
import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

public class TextContentHandlerTest {

    static ContentHandler<Text> contentHandler;

    @BeforeAll
    public static void beforeAll() {
        contentHandler = new TextContentHandler();
    }

    @Test
    public void test_read_activate() {
        expect:
        assertTrue(contentHandler.activateReader(TextEndpoint.class, null));
    }

    @Test
    public void test_write_activate() {
        expect:
        assertTrue(contentHandler.activateWriter(TextEndpoint.class, null));
    }

    @Test
    public void test_read() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl(new ByteArrayInputStream("ok".getBytes()));
        Text text;

        when:
        text = contentHandler.read(httpExchange, TextEndpoint.class);

        then:
        assertEquals("ok", text.get());
    }

    @Test
    public void test_write() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        when:
        contentHandler.write(200, new Text("ok"), httpExchange, TextEndpoint.class);

        then:
        assertEquals(httpExchange.getResponseCode(), 200);
        assertEquals(MediaType.TEXT_PLAIN, httpExchange.getResponseHeaders().get("Content-Type").get(0));
        assertEquals("ok", new String(((ByteArrayOutputStream) httpExchange.getResponseBody()).toByteArray()));
    }

    public static class TextEndpoint implements HttpEndpoint<Text, Text> {

        @Override
        public Text response(Request<Text> request) {
            return new Text(request.getBody().get() + "!");
        }
    }

}
