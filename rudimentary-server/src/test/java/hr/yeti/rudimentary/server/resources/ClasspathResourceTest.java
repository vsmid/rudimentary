package hr.yeti.rudimentary.server.resources;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ClasspathResourceTest {

    @ParameterizedTest
    @ValueSource(strings = { "/templates/apidocs.html", "templates/apidocs.html" })
    public void test_get_resource(String path) {
        expect: assertNotNull(new ClasspathResource(path).get());
    }

}
