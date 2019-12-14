package hr.yeti.rudimentary.test.context;

import hr.yeti.rudimentary.test.context.mock.MockInstance1;
import hr.yeti.rudimentary.test.ContextMock;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.test.ConfigMock;
import hr.yeti.rudimentary.test.context.mock.MockInstance11a;
import hr.yeti.rudimentary.test.context.mock.MockInstance11b;
import hr.yeti.rudimentary.test.context.mock.MockInstance2;
import hr.yeti.rudimentary.test.context.mock.MockInstance3;
import hr.yeti.rudimentary.test.context.mock.MockInstance4;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class InstanceTest {

    @Test
    public void test_should_throw_exception_when_instance_does_not_exist() {
        // setup:
        ContextMock ctx = new ContextMock(
            Map.of(),
            MockInstance1.class
        );

        Instance fetchedInstance;

        when:   fetchedInstance = Instance.of(MockInstance2.class);

        then:   assertNull(fetchedInstance);
    }

    @Test
    public void test_should_return_a_single_instance_of_spi() {
        // setup:
        ContextMock ctx = new ContextMock(
            Map.of(),
            MockInstance1.class
        );

        Instance fetchedInstance;

        when:   fetchedInstance = Instance.of(MockInstance1.class);

        then:   assertNotNull(fetchedInstance);
        assertTrue(fetchedInstance instanceof MockInstance1);
    }

    @Test
    public void test_should_return_first_instance_initialized_when_there_are_no_primary_instances() {
        // setup:
        ContextMock ctx = new ContextMock(
            Map.of(),
            MockInstance4.class,
            MockInstance2.class
        );

        Instance fetchedInstance;

        when:   fetchedInstance = Instance.of(Instance.class);

        then:   assertNotNull(fetchedInstance);
        assertTrue(fetchedInstance instanceof ConfigMock);
    }

    @Test
    public void test_should_return_an_instance_marked_as_primary() {
        // setup:
        ContextMock ctx = new ContextMock(
            Map.of(),
            MockInstance1.class,
            MockInstance2.class
        );

        Instance fetchedInstance;

        when:   fetchedInstance = Instance.of(Instance.class);

        then:   assertNotNull(fetchedInstance);
        assertTrue(fetchedInstance instanceof MockInstance1);
    }

    @Test
    public void test_should_return_an_instance_initialized_before_on_multiple_primary_providers() {
        // setup:

        ContextMock ctx = new ContextMock(
            Map.of(),
            MockInstance1.class,
            MockInstance3.class
        );

        Instance fetchedInstance;

        when:   fetchedInstance = Instance.of(Instance.class);

        then:   assertNotNull(fetchedInstance);
        assertTrue(fetchedInstance instanceof MockInstance1);

        and:    when:   ctx = new ContextMock(
                            Map.of(),
                            MockInstance3.class,
                            MockInstance1.class
                        );

        and:    when:   fetchedInstance = Instance.of(Instance.class);

        then:   assertNotNull(fetchedInstance);
        assertTrue(fetchedInstance instanceof MockInstance3);
    }

    @Test
    public void test_should_return_all_providers_of_the_given_spi() {
        // setup:
        ContextMock ctx = new ContextMock(
            Map.of(),
            MockInstance1.class,
            MockInstance2.class,
            MockInstance3.class
        );

        List<Instance> fetchedInstances;

        when:   fetchedInstances = Instance.providersOf(Instance.class);

        then:   assertNotNull(fetchedInstances);
        assertTrue(fetchedInstances.size() == 4); // ConfigMock also
    }

    @Test
    public void test_should_return_an_instance_with_id() {
        // setup:
        ContextMock ctx = new ContextMock(
            Map.of(),
            MockInstance11a.class,
            MockInstance11b.class
        );

        Instance fetchedInstance;

        when:   fetchedInstance = Instance.withId(MockInstance11a.class, "lena");

        then:   assertNotNull(fetchedInstance);
        assertTrue(fetchedInstance instanceof MockInstance11a);
        assertEquals("lena", fetchedInstance.id());
    }
}
