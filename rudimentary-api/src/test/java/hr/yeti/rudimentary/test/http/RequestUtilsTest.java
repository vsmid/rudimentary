package hr.yeti.rudimentary.test.http;

import hr.yeti.rudimentary.http.HttpRequestUtils;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.mvc.spi.ViewEndpoint;
import java.net.URI;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class RequestUtilsTest {

    @Test
    public void test_parse_uri_path_variables_when_endpoint_uri_is_not_prefixed_with_slash() {
        Map<String, String> pathVariables;

        when:
        pathVariables = HttpRequestUtils.parsePathVariables(URI.create("cars/:id"), URI.create("/cars/1"));

        then:
        assertNotNull(pathVariables);

        assertTrue(pathVariables.containsKey("id"));
        assertEquals("1", pathVariables.get("id"));
    }

    @Test
    public void test_parse_uri_path_variables_when_endpoint_uri_is_prefixed_with_slash() {
        Map<String, String> pathVariables;

        when:
        pathVariables = HttpRequestUtils.parsePathVariables(URI.create("/cars/:id/type/:name"), URI.create("/cars/1/type/bmw"));

        then:
        assertNotNull(pathVariables);

        assertTrue(pathVariables.containsKey("id"));
        assertEquals("1", pathVariables.get("id"));

        assertTrue(pathVariables.containsKey("name"));
        assertEquals("bmw", pathVariables.get("name"));
    }

    @Test
    public void test_parse_uri_query_params() {
        Map<String, Object> queryParameters;

        when:
        queryParameters = HttpRequestUtils.parseQueryParameters(URI.create("/cars?name=Martina&dob=09.06.1986").getQuery());

        then:
        assertNotNull(queryParameters);

        assertTrue(queryParameters.containsKey("name"));
        assertEquals("Martina", queryParameters.get("name"));

        assertTrue(queryParameters.containsKey("dob"));
        assertEquals("09.06.1986", queryParameters.get("dob"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "/cars", "/cars?" })
    public void test_empty_map_on_no_query_parameters(String uris) {
        Map<String, Object> queryParameters;

        when:
        queryParameters = HttpRequestUtils.parseQueryParameters(URI.create(uris).getQuery());

        then:
        assertNotNull(queryParameters);
        assertTrue(queryParameters.isEmpty());
    }

    @Test
    public void test_requestBodyType_method() throws ClassNotFoundException {
        expect:
        assertEquals(Empty.class, HttpRequestUtils.getRequestBodyType(new CustomEndpoint().getClass()));
    }

    @Test
    public void test_is_view_endpoint() {
        expect:

        assertEquals(true, HttpRequestUtils.isViewEndpoint(CustomViewEndpoint.class));
    }

    public static class CustomViewEndpoint implements ViewEndpoint<Empty> {

        @Override
        public View response(Request<Empty> request) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public static class CustomEndpoint implements HttpEndpoint<Empty, Text> {

        @Override
        public Text response(Request<Empty> request) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
