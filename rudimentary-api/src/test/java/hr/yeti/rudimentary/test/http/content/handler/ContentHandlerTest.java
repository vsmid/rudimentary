package hr.yeti.rudimentary.test.http.content.handler;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.mvc.spi.ViewEndpoint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ContentHandlerTest {

    @Test
    public void test_requestBodyType_method() throws ClassNotFoundException {
        expect:
        assertEquals(Empty.class, ContentHandler.getGenericType(new CustomEndpoint().getClass(), 0));
    }

    @Test
    public void test_responseBodyType_method() throws ClassNotFoundException {
        expect:
        assertEquals(Text.class, ContentHandler.getGenericType(new CustomEndpoint().getClass(), 1));
    }

    @Test
    public void test_is_view_endpoint() {
        expect:

        assertEquals(true, ContentHandler.isViewEndpoint(CustomViewEndpoint.class));
    }

    public static class CustomViewEndpoint implements ViewEndpoint<Empty> {

        @Override
        public View response(Request<Empty> request) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    public static class CustomEndpoint implements HttpEndpoint<Empty, Text> {

        @Override
        public Text response(Request<Empty> request) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
}
