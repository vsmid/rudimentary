package hr.yeti.rudimentary.server.http.content.handler;

import hr.yeti.rudimentary.server.http.TestHttpExchangeImpl;
import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.ByteStream;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

public class ByteStreamContentHandlerTest {

    static ContentHandler<ByteStream> contentHandler;

    @BeforeAll
    public static void beforeAll() {
        contentHandler = new ByteStreamContentHandler();
    }

    @Test
    public void test_read_activate() {
        expect:
        assertTrue(contentHandler.activateReader(ByteStreamEndpoint.class, null));
    }

    @Test
    public void test_write_activate() {
        expect:
        assertTrue(contentHandler.activateWriter(ByteStreamEndpoint.class, null));
    }

    @Test
    public void test_read() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl(new ByteArrayInputStream("ok".getBytes()));
        ByteStream byteStream;

        when:
        byteStream = contentHandler.read(httpExchange, ByteStreamEndpoint.class);

        then:
        assertEquals("ok", new String(byteStream.get().readAllBytes()));
    }

    @Test
    public void test_write() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        when:
        contentHandler.write(200, new ByteStream((os) -> {
            os.write("1".getBytes());
        }), httpExchange, ByteStreamEndpoint.class);

        then:
        assertEquals(httpExchange.getResponseCode(), 200);
        assertEquals("chunked", httpExchange.getResponseHeaders().get("Transfer-Encoding").get(0));
        assertEquals(new String(((ByteArrayOutputStream) httpExchange.getResponseBody()).toByteArray()), "1");
    }

    public static class ByteStreamEndpoint implements HttpEndpoint<ByteStream, ByteStream> {

        @Override
        public ByteStream response(Request<ByteStream> request) {
            return null;
        }
    }

}
