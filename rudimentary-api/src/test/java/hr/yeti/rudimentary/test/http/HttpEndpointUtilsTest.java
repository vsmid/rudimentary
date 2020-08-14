package hr.yeti.rudimentary.test.http;

import hr.yeti.rudimentary.http.HttpEndpointUtils;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.mvc.spi.ViewEndpoint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class HttpEndpointUtilsTest {

    @Test
    public void test_requestBodyType_method() throws ClassNotFoundException {
        expect:
        assertEquals(Empty.class, HttpEndpointUtils.getRequestBodyType(new CustomEndpoint().getClass()));
    }

    @Test
    public void test_responseBodyType_method() throws ClassNotFoundException {
        expect:
        assertEquals(Text.class, HttpEndpointUtils.getResponseBodyType(new CustomEndpoint().getClass()));
    }

    @Test
    public void test_is_view_endpoint() {
        expect:

        assertEquals(true, HttpEndpointUtils.isViewEndpoint(CustomViewEndpoint.class));
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
