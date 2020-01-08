package hr.yeti.rudimentary.server;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.server.test.TestServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InstanceTest {

    static TestServer testServer;

    @BeforeAll
    public static void beforeAll() {
        testServer = TestServer.newBuilder()
            .instances(_Instances.SimpleInstance.class)
            .build();
        testServer.start();
    }

    @AfterAll
    public static void afterAll() {
        testServer.stop();
    }

    @Test
    public void test_instance_initialized() throws InterruptedException {
        // setup:
        then:
        assertNotNull(Instance.of(_Instances.SimpleInstance.class));
    }
}
