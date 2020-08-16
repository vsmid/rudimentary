package hr.yeti.rudimentary.server.http.content.handler;

import hr.yeti.rudimentary.server.http.TestHttpExchangeImpl;
import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Form;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

public class FormContentHandlerTest {

    static ContentHandler<Form> contentHandler;

    @BeforeAll
    public static void beforeAll() {
        contentHandler = new FormContentHandler();
    }

    @Test
    public void test_read_activate() {
        expect:
        assertTrue(contentHandler.activateReader(FormEndpoint.class, null));
    }

    @Test
    public void test_write_activate() {
        expect:
        assertTrue(contentHandler.activateWriter(FormEndpoint.class, null));
    }

    @Test
    public void test_read() throws IOException {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl(new ByteArrayInputStream("name=Lena&age=1".getBytes()));
        Form form;

        when:
        form = contentHandler.read(httpExchange, FormEndpoint.class);

        then:
        assertEquals(
            new TreeMap<>(Map.of("name", "Lena", "age", "1")).toString(),
            new TreeMap<>(form.get()).toString()
        );
    }

    @Test
    public void test_write() {
        // setup:
        HttpExchange httpExchange = new TestHttpExchangeImpl();

        expect:
        assertThrows(java.lang.UnsupportedOperationException.class, () -> {
            contentHandler.write(200, new Form(Map.of()), httpExchange, FormEndpoint.class);
        });
    }

    public static class FormEndpoint implements HttpEndpoint<Form, Form> {

        @Override
        public Form response(Request<Form> request) {
            return new Form(Map.of());
        }
    }

}
