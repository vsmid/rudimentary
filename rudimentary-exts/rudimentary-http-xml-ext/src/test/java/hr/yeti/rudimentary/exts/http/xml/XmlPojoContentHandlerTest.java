package hr.yeti.rudimentary.exts.http.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Pojo;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.bind.annotation.XmlRootElement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

public class XmlPojoContentHandlerTest {

    static ContentHandler<Pojo> contentHandler;

    @BeforeAll
    public static void beforeAll() {
        contentHandler = new XmlPojoContentHandler();
    }

    @Test
    public void test_read_activate() {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        when:
        httpExchange.getRequestHeaders().add("Content-Type", MediaType.APPLICATION_XML);

        then:
        assertTrue(contentHandler.activateReader(XmlPojoEndpoint.class, httpExchange));
    }

    @Test
    public void test_write_activate() {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        expect:
        assertTrue(contentHandler.activateWriter(XmlPojoEndpoint.class, httpExchange));
    }

    @Test
    public void test_read() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl(new ByteArrayInputStream("<inOut><name>Lena</name></inOut>".getBytes()));
        InOut inOut;

        when:
        inOut = (InOut) contentHandler.read(httpExchange, XmlPojoEndpoint.class);

        then:
        assertEquals("Lena", inOut.getName());
    }

    @Test
    public void test_write() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        when:
        contentHandler.write(200, new InOut("Lena"), httpExchange, XmlPojoEndpoint.class);

        then:
        assertEquals(httpExchange.getResponseCode(), 200);
        assertEquals(MediaType.APPLICATION_XML, httpExchange.getResponseHeaders().get("Content-Type").get(0));
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><inOut><name>Lena</name></inOut>", new String(((ByteArrayOutputStream) httpExchange.getResponseBody()).toByteArray()));
    }

    public static class XmlPojoEndpoint implements HttpEndpoint<InOut, InOut> {

        @Override
        public InOut response(Request<InOut> request) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    @XmlRootElement
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
