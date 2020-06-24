package hr.yeti.rudimentary.test.http;

import hr.yeti.rudimentary.exception.ExceptionInfo;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import java.net.URI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hr.yeti.rudimentary.http.content.Model;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpEndpointTest {

    private OkEndpoint okEndpoint;

    @BeforeEach
    public void beforeEach() {
        okEndpoint = new OkEndpoint();
    }

    @Test
    @DisplayName("Default path() method should return simple class name with first letter lowercased")
    public void test_path_method() {
        expect:
        assertEquals("/okEndpoint", okEndpoint.path());
    }

    @Test
    @DisplayName("Default successHttpStatus() method should return 200")
    public void test_successHttpStatus_method() {
        expect:
        assertEquals(200, okEndpoint.httpStatus());
    }

    @Test
    @DisplayName("Default onException() method should return 500 - Internal Server Error")
    public void test_onException_method() {
        ExceptionInfo exceptionInfo;

        when:
        exceptionInfo = okEndpoint.onException(new RuntimeException());

        then:
        assertEquals(500, exceptionInfo.getHttpStatus());
        assertEquals("Internal Server Error.", exceptionInfo.getDescription());
    }

    @Test
    @DisplayName("Default responseHttpHeaders() should return empty instance of Map")
    public void test_responseHttpHeaders_method() {
        expect:
        assertNotNull(okEndpoint.responseHttpHeaders(null, null));
        assertTrue(okEndpoint.responseHttpHeaders(null, null).isEmpty());
    }

    @Test
    @DisplayName("Default httpMethod() should return GET")
    public void test_httpMethod_method() {
        expect:
        assertEquals(HttpMethod.GET, okEndpoint.httpMethod());
    }

    @Test
    @DisplayName("Default logger should return an instance of System.Logger")
    public void test_logger_method() {
        expect:
        assertTrue(okEndpoint.logger() instanceof System.Logger);
    }

    public class OkEndpoint implements HttpEndpoint<OkModel, Text> {

        @Override
        public Text response(Request<OkModel> request) {
            return new Text("Ok");
        }

    }

    public static class OkModel extends Model {

    }

}
