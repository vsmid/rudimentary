package hr.yeti.rudimentary.test.context;

import hr.yeti.rudimentary.test.context.mock.ContextMock;
import hr.yeti.rudimentary.test.context.mock.MockInstance1;
import hr.yeti.rudimentary.test.context.mock.MockInstance2;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    assertTrue(ContextMock.getContext().containsKey(MockInstance1.class.getCanonicalName()));

    assertTrue(ContextMock.getInitializedInstances().size() == 1);
    assertTrue(ContextMock.getInitializedInstances().contains(MockInstance1.class.getCanonicalName()));

    and:

    when:
    // Calls destroy method in constructor.
    ctx = new ContextMock();

    then:
    assertTrue(ContextMock.getContext().isEmpty());
    assertTrue(ContextMock.getInitializedInstances().isEmpty());
  }

  @Test
  public void test_should_add_instance_to_context_map_and_to_a_list_of_initialized_instances() {
    // setup: 
    MockInstance1 mockInstance1 = new MockInstance1();
    ContextMock ctx = new ContextMock(mockInstance1);

    expect:
    assertTrue(ContextMock.getContext().size() == 1);
    assertTrue(ContextMock.getContext().containsKey(MockInstance1.class.getCanonicalName()));

    assertTrue(ContextMock.getInitializedInstances().size() == 1);
    assertTrue(ContextMock.getInitializedInstances().contains(MockInstance1.class.getCanonicalName()));
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

}
