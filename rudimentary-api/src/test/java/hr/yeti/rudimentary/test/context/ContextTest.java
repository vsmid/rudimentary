package hr.yeti.rudimentary.test.context;

import hr.yeti.rudimentary.context.spi.ContextException;
import hr.yeti.rudimentary.test.ContextMock;
import hr.yeti.rudimentary.test.context.mock.MockInstance1;
import hr.yeti.rudimentary.test.context.mock.MockInstance10;
import hr.yeti.rudimentary.test.context.mock.MockInstance2;
import hr.yeti.rudimentary.test.context.mock.MockInstance5;
import hr.yeti.rudimentary.test.context.mock.MockInstance6;
import hr.yeti.rudimentary.test.context.mock.MockInstance7;
import hr.yeti.rudimentary.test.context.mock.MockInstance8;
import hr.yeti.rudimentary.test.context.mock.MockInstance9a;
import hr.yeti.rudimentary.test.context.mock.MockInstance9b;
import hr.yeti.rudimentary.test.context.mock.MockInstance9c;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class ContextTest {

    @Test
    public void test_initial_context_state() {
        // setup:
        ContextMock ctx = new ContextMock(Map.of());

        expect:
        assertNotNull(ContextMock.acquire());
        assertFalse(ContextMock.acquire().isEmpty());

        assertNotNull(ContextMock.getInitializedInstances());
        assertFalse(ContextMock.getInitializedInstances().isEmpty());
    }

    @Test
    public void test_destroy_method_should_reset_state() {
        // setup: 
        ContextMock ctx;

        when:
        ctx = new ContextMock(Map.of(), MockInstance1.class);

        then:
        assertTrue(ContextMock.acquire().size() == 2);
        assertTrue(ContextMock.acquire().containsKey(MockInstance1.class.getCanonicalName()));

        assertTrue(ContextMock.getInitializedInstances().size() == 2);
        assertTrue(ContextMock.getInitializedInstances().contains(MockInstance1.class.getCanonicalName()));

        and:
        when: // Calls destroy method in constructor.
        ctx = new ContextMock(Map.of());

        then:
        assertEquals(1, ContextMock.acquire().size());
        assertEquals(1, ContextMock.getInitializedInstances().size());
    }

    @Test
    public void test_should_execute_all_initialization_steps() {
        // setup: 
        ContextMock ctx = new ContextMock(Map.of(), MockInstance1.class);

        expect: // Value is 10 after initialization.
        assertEquals("10", ((MockInstance1) ContextMock.acquire().get(MockInstance1.class.getCanonicalName())).getValue());

        //Instance is put to context map.
        assertTrue(ContextMock.acquire().size() == 2);
        assertTrue(ContextMock.acquire().containsKey(MockInstance1.class.getCanonicalName()));

        // Instance is initialized.
        assertTrue(ContextMock.getInitializedInstances().size() == 2);
        assertTrue(ContextMock.getInitializedInstances().contains(MockInstance1.class.getCanonicalName()));
    }

    @Test
    public void test_isInstanceInitialized_should_return_correct_result() {
        // setup:
        ContextMock ctx = new ContextMock(Map.of(), MockInstance1.class);

        expect: // contextMock.initialized is calling protected Context method in order to test it.
        assertTrue(ctx.initialized(MockInstance1.class));
        assertFalse(ctx.initialized(MockInstance2.class));
    }

    @Test
    public void test_creating_instance_dependency_graph() {
        // setup:
        ContextMock ctx = new ContextMock(Map.of(), MockInstance1.class, MockInstance5.class);

        expect:
        assertEquals(3, ctx.getInstanceDependencyGraph().size());
        assertTrue(ctx.getInstanceDependencyGraph().get(MockInstance1.class.getCanonicalName()).isEmpty());
        assertEquals(1, ctx.getInstanceDependencyGraph().get(MockInstance5.class.getCanonicalName()).size());
        assertEquals(MockInstance2.class.getCanonicalName(), ctx.getInstanceDependencyGraph().get(MockInstance5.class.getCanonicalName()).get(0));
    }

    @Test
    public void test_exception_on_circular_dependency() {
        ContextException ex;

        expect:
        ex = assertThrows(ContextException.class, () -> new ContextMock(Map.of(), MockInstance6.class, MockInstance7.class));
        assertTrue(ex.getMessage().startsWith("Circular dependency detected"));
    }

    @Test
    public void test_exception_on_self_circular_dependency() {
        ContextException ex;

        expect:
        ex = assertThrows(ContextException.class, () -> new ContextMock(Map.of(), MockInstance8.class));
        assertTrue(ex.getMessage().startsWith("Circular dependency detected"));
    }

    @Test
    public void test_exception_on_transitive_circular_dependency() {
        ContextException ex;

        expect:
        ex = assertThrows(ContextException.class, () -> new ContextMock(Map.of(), MockInstance9a.class, MockInstance9b.class, MockInstance9c.class));
        assertTrue(ex.getMessage().startsWith("Circular dependency detected"));
    }

    @Test
    public void test_config_to_mock_context_injection() {
        ContextMock ctx;

        when:
        ctx = new ContextMock(Map.of("val", "Hello World"), MockInstance10.class);

        then:
        assertEquals("Hello World", ((MockInstance10) ContextMock.acquire().get(MockInstance10.class.getCanonicalName())).getVal());
    }
}
