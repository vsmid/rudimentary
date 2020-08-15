package hr.yeti.rudimentary.server.http.content.handler;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
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

    public static class EmptyEndpoint implements HttpEndpoint<Empty, Empty> {

        @Override
        public Empty response(Request<Empty> request) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

}
