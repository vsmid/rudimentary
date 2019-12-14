package hr.yeti.rudimentary.test.http;

import hr.yeti.rudimentary.http.URIUtils;
import java.net.URI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class URIUtilsTest {

    @Test
    public void test_converting_uri_to_regex() {
        // setup:
        URI uri1 = URI.create("/cars/:id");
        URI uri2 = URI.create("/cars/:id/owners/:ownerId");

        expect: assertEquals("^/cars/([^/.]+)$", URIUtils.convertToRegex(uri1.toString(), "([^/.]+)").toString());
        assertEquals("^/cars/([^/.]+)/owners/([^/.]+)$", URIUtils.convertToRegex(uri2.toString(), "([^/.]+)").toString());
    }

    @Test
    public void test_remove_prefix_slash() {
        // setup:
        URI uri1 = URI.create("uri1");
        URI uri2 = URI.create("/uri2");
        URI uri3 = URI.create("/uri3?name=rudimentary");

        expect: assertEquals("uri1", URIUtils.removeSlashPrefix(uri1).toString());
        assertEquals("uri2", URIUtils.removeSlashPrefix(uri2).toString());
        assertEquals("uri3", URIUtils.removeSlashPrefix(uri3).toString());
    }

    @Test
    public void test_prepend_slash() {
        // setup:
        URI uri1 = URI.create("uri1");
        URI uri2 = URI.create("/uri2");

        expect: assertEquals("/uri1", URIUtils.prependSlashPrefix(uri1).toString());
        assertEquals("/uri2", URIUtils.prependSlashPrefix(uri2).toString());
    }

}
