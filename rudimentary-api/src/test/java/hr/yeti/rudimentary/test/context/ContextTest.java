package hr.yeti.rudimentary.test.context;

import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.ContextException;
import hr.yeti.rudimentary.test.ConfigMock;
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
    ContextMock ctx = new ContextMock();

    expect:
    assertNotNull(ContextMock.getContext());
    assertTrue(ContextMock.getContext().isEmpty());

    assertNotNull(ContextMock.getInitializedInstances());
    assertTrue(ContextMock.getInitializedInstances().isEmpty());
  }

  @Test
  public void test_destroy_method_should_reset_state() {
    // setup: 
    MockInstance1 mockInstance1 = new MockInstance1();
    ContextMock ctx;

    when:
    ctx = new ContextMock(mockInstance1);

    then:
    assertTrue(ContextMock.getContext().size() == 1);
    assertTrue(ContextMock.getContext().containsKey(MockInstance1.class.getSimpleName().toLowerCase()));

    assertTrue(ContextMock.getInitializedInstances().size() == 1);
    assertTrue(ContextMock.getInitializedInstances().contains(MockInstance1.class.getSimpleName().toLowerCase()));

    and:

    when:
    // Calls destroy method in constructor.
    ctx = new ContextMock();

    then:
    assertTrue(ContextMock.getContext().isEmpty());
    assertTrue(ContextMock.getInitializedInstances().isEmpty());
  }

  @Test
  public void test_should_execute_all_initialization_steps() {
    // setup: 
    MockInstance1 mockInstance1 = new MockInstance1();
    ContextMock ctx = new ContextMock(mockInstance1);

    expect:
    // Value is 10 after initialization.
    assertEquals("10", mockInstance1.getValue());

    //Instance is put to context map.
    assertTrue(ContextMock.getContext().size() == 1);
    assertTrue(ContextMock.getContext().containsKey(MockInstance1.class.getSimpleName().toLowerCase()));

    // Instance is initialized.
    assertTrue(ContextMock.getInitializedInstances().size() == 1);
    assertTrue(ContextMock.getInitializedInstances().contains(MockInstance1.class.getSimpleName().toLowerCase()));
  }

  @Test
  public void test_isInstanceInitialized_should_return_correct_result() {
    // setup:
    ContextMock ctx = new ContextMock(new MockInstance1());

    expect:
    // contextMock.initialized is calling protected Context method in order to test it.
    assertTrue(ctx.initialized(MockInstance1.class));
    assertFalse(ctx.initialized(MockInstance2.class));
  }

  @Test
  public void test_creating_instance_dependency_graph() {
    // setup:
    ContextMock ctx = new ContextMock(new MockInstance1(), new MockInstance5());

    expect:
    assertEquals(2, ctx.getInstanceDependencyGraph().size());
    assertTrue(ctx.getInstanceDependencyGraph().get(MockInstance1.class.getSimpleName().toLowerCase()).isEmpty());
    assertEquals(1, ctx.getInstanceDependencyGraph().get(MockInstance5.class.getSimpleName().toLowerCase()).size());
    assertEquals(MockInstance2.class.getSimpleName().toLowerCase(), ctx.getInstanceDependencyGraph().get(MockInstance5.class.getSimpleName().toLowerCase()).get(0));
  }

  @Test
  public void test_exception_on_circular_dependency() {
    ContextException ex;

    expect:
    ex = assertThrows(ContextException.class, () -> new ContextMock(new MockInstance6(), new MockInstance7()));
    assertTrue(ex.getMessage().startsWith("Circular dependency detected"));
  }

  @Test
  public void test_exception_on_self_circular_dependency() {
    ContextException ex;

    expect:
    ex = assertThrows(ContextException.class, () -> new ContextMock(new MockInstance8()));
    assertTrue(ex.getMessage().startsWith("Circular dependency detected"));
  }

  @Test
  public void test_exception_on_transitive_circular_dependency() {
    ContextException ex;

    expect:
    ex = assertThrows(ContextException.class, () -> new ContextMock(new MockInstance9a(), new MockInstance9b(), new MockInstance9c()));
    assertTrue(ex.getMessage().startsWith("Circular dependency detected"));
  }

  @Test
  public void test_config_to_mock_context_injection() {
    ContextMock ctx;
    Config config = new ConfigMock();

    when:
    config = config.load(new ConfigProperty("val", "Hello World"));
    config.seal();
    ctx = new ContextMock(config, new MockInstance10());

    then:
    assertEquals("Hello World", ((MockInstance10) ContextMock.getContext().get(MockInstance10.class.getSimpleName().toLowerCase())).getVal());
  }
}
