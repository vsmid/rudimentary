package hr.yeti.rudimentary.test.i18n;

import hr.yeti.rudimentary.i18n.I18n;
import hr.yeti.rudimentary.test.ContextMock;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class I18nTest {

    @Test
    public void test_i18n() {
        // setup:
        new ContextMock(
            Map.of(
                "i18n.bundles", "classpath:messages"
            )
        );

        expect:
        assertEquals(I18n.text("test"), "test1");
    }

    @Test
    public void test_i18n_by_language() {
        // setup:
        new ContextMock(
            Map.of(
                "i18n.bundles", "classpath:messages"
            )
        );

        expect:
        assertEquals(I18n.text("test", Locale.forLanguageTag("hr")), "test2");
    }

    @Test
    public void test_i18n_defaultMessage() {
        // setup:
        new ContextMock(
            Map.of(
                "i18n.bundles", "classpath:messages"
            )
        );

        expect:
        assertEquals(I18n.text("noKey", "No key."), "No key.");
    }

    @Test
    public void test_i18n_param_injection() {
        // setup:
        new ContextMock(
            Map.of(
                "i18n.bundles", "classpath:messages"
            )
        );

        expect:
        assertEquals(I18n.text("test.param", 1), "param=1");
    }
}
