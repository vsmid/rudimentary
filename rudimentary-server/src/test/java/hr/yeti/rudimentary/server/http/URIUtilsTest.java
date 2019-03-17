package hr.yeti.rudimentary.server.http;

import java.net.URI;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class URIUtilsTest {

  @Test
  public void test_parse_uri_path_variables() {
    Map<String, String> pathVariables;

    when:
    pathVariables = URIUtils.parsePathVariables(URI.create("/cars/:id/type/:name"), URI.create("/cars/1/type/bmw"));

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
    queryParameters = URIUtils.parseQueryParameters(URI.create("/cars?name=Martina&dob=09.06.1986").getQuery());

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
    queryParameters = URIUtils.parseQueryParameters(URI.create(uris).getQuery());

    then:
    assertNotNull(queryParameters);
    assertTrue(queryParameters.isEmpty());
  }

  @Test
  public void test_uri_path_should_remove_prefix_slash() {
    // setup:
    URI uri1 = URI.create("uri1");
    URI uri2 = URI.create("/uri2");
    URI uri3 = URI.create("/uri3?name=rudimentary");

    expect:
    assertEquals("uri1", URIUtils.removeSlashPrefix(uri1).toString());
    assertEquals("uri2", URIUtils.removeSlashPrefix(uri2).toString());
  }

}
