package hr.yeti.rudimentary.server.http.content.handler;

import hr.yeti.rudimentary.server.http.TestHttpExchangeImpl;
import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Pojo;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

public class JsonPojoContentHandlerTest {

    static ContentHandler<Pojo> contentHandler;

    @BeforeAll
    public static void beforeAll() {
        contentHandler = new JsonPojoContentHandler();
    }

    @Test
    public void test_read_activate() {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        when:
        httpExchange.getRequestHeaders().add("Content-Type", MediaType.APPLICATION_JSON);

        then:
        assertTrue(contentHandler.activateReader(JsonPojoEndpoint.class, httpExchange));
    }

    @Test
    public void test_write_activate() {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();
        
        expect:
        assertTrue(contentHandler.activateWriter(JsonPojoEndpoint.class, httpExchange));
    }

    @Test
    public void test_read() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl(new ByteArrayInputStream("{\"name\":\"Lena\"}".getBytes()));
        InOut inOut;

        when:
        inOut = (InOut) contentHandler.read(httpExchange, JsonPojoEndpoint.class);

        then:
        assertEquals("Lena", inOut.getName());
    }

    @Test
    public void test_write() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        when:
        contentHandler.write(200, new InOut("Lena"), httpExchange, JsonPojoEndpoint.class);

        then:
        assertEquals(httpExchange.getResponseCode(), 200);
        assertEquals(MediaType.APPLICATION_JSON, httpExchange.getResponseHeaders().get("Content-Type").get(0));
        assertEquals("{\"name\":\"Lena\"}", new String(((ByteArrayOutputStream) httpExchange.getResponseBody()).toByteArray()));
    }

    public static class JsonPojoEndpoint implements HttpEndpoint<InOut, InOut> {

        @Override
        public InOut response(Request<InOut> request) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    public static class InOut extends Pojo {

        private String name;

        public InOut() {
        }

        public InOut(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

}
