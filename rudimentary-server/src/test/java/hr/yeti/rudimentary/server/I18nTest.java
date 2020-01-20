package hr.yeti.rudimentary.server;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.i18n.I18n;
import hr.yeti.rudimentary.server.i18n.MultiResourceBundle;
import hr.yeti.rudimentary.server.test.TestServer;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class I18nTest {

    @Test
    public void test_i18n_instance_initialized() throws InterruptedException {
        // setup:
        TestServer testServer = TestServer.newBuilder()
            .config(Map.of("i18n.bundles", "classpath:messages"))
            .build();
        testServer.start();

        then:
        assertNotNull(Instance.of(MultiResourceBundle.class));
        assertEquals("I am 35 years old.", I18n.text("test", 35));

        testServer.stop();
    }

    @Test
    public void test_i18n_localization() throws InterruptedException {
        // setup:
        TestServer testServer = TestServer.newBuilder()
            .config(
                Map.of(
                    "i18n.locale", "hr",
                    "i18n.bundles", "classpath:messages"
                )
            )
            .build();
        testServer.start();

        then:
        assertNotNull(Instance.of(MultiResourceBundle.class));
        assertEquals("Ja imam 35 godina.", I18n.text("test", 35));

        testServer.stop();
    }
}
