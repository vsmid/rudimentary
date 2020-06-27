/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.yeti.rudimentary.test.http;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Text;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class RequestTest {

    @Test
    public void get_path_variable() {
        // setup:
        Request<Text> request = new Request<>(null, null, null, Map.of("title", "pathVar"), null, null, null);

        expect:
        assertEquals("pathVar", request.pathVar("title").value());
    }

    @Test
    public void get_query_parameter() {
        // setup:
        Request<Text> request = new Request<>(null, null, null, null, Map.of("title", "queryParam"), null, null);

        expect:
        assertEquals("queryParam", request.queryParam("title").value());
    }

}
