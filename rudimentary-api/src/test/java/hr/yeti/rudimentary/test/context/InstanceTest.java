package hr.yeti.rudimentary.test.context;

import hr.yeti.rudimentary.test.context.mock.MockInstance1;
import hr.yeti.rudimentary.test.ContextMock;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.test.context.mock.MockInstance2;
import hr.yeti.rudimentary.test.context.mock.MockInstance3;
import hr.yeti.rudimentary.test.context.mock.MockInstance4;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class InstanceTest {

  @Test
  public void test_should_return_null_when_instance_does_not_exist() {
    // setup:
    MockInstance1 mockInstance1 = new MockInstance1();

    ContextMock ctx = new ContextMock(
        mockInstance1
    );

    Instance fetchedInstance;

    when:
    fetchedInstance = Instance.of(MockInstance2.class);

    then:
    assertNull(fetchedInstance);
  }

  @Test
  public void test_should_return_a_single_instance_of_spi() {
    // setup:
    MockInstance1 mockInstance1 = new MockInstance1();

    ContextMock ctx = new ContextMock(
        mockInstance1
    );

    Instance fetchedInstance;

    when:
    fetchedInstance = Instance.of(MockInstance1.class);

    then:
    assertNotNull(fetchedInstance);
    assertTrue(fetchedInstance instanceof MockInstance1);
    assertEquals(mockInstance1, fetchedInstance);
  }

  @Test
  public void test_should_return_first_instance_initialized_when_there_are_no_primary_instances() {
    // setup:
    MockInstance2 mockInstance2 = new MockInstance2();
    MockInstance4 mockInstance4 = new MockInstance4();

    ContextMock ctx = new ContextMock(
        mockInstance4,
        mockInstance2
    );

    Instance fetchedInstance;

    when:
    fetchedInstance = Instance.of(Instance.class);

    then:
    assertNotNull(fetchedInstance);
    assertTrue(fetchedInstance instanceof MockInstance4);
    assertEquals(mockInstance4, fetchedInstance);
  }

  @Test
  public void test_should_return_an_instance_marked_as_primary() {
    // setup:
    MockInstance1 mockInstance1 = new MockInstance1();
    MockInstance2 mockInstance2 = new MockInstance2();

    ContextMock ctx = new ContextMock(
        mockInstance1,
        mockInstance2
    );

    Instance fetchedInstance;

    when:
    fetchedInstance = Instance.of(Instance.class);

    then:
    assertNotNull(fetchedInstance);
    assertTrue(fetchedInstance instanceof MockInstance1);
    assertEquals(mockInstance1, fetchedInstance);
  }

  @Test
  public void test_should_return_an_instance_initialized_before_on_multiple_primary_providers() {
    // setup:
    MockInstance1 mockInstance1 = new MockInstance1();
    MockInstance3 mockInstance3 = new MockInstance3();

    ContextMock ctx = new ContextMock(
        mockInstance1,
        mockInstance3
    );

    Instance fetchedInstance;

    when:
    fetchedInstance = Instance.of(Instance.class);

    then:
    assertNotNull(fetchedInstance);
    assertTrue(fetchedInstance instanceof MockInstance1);
    assertEquals(mockInstance1, fetchedInstance);

    and:

    when:
    ctx = new ContextMock(
        mockInstance3,
        mockInstance1
    );

    and:

    when:
    fetchedInstance = Instance.of(Instance.class);

    then:
    assertNotNull(fetchedInstance);
    assertTrue(fetchedInstance instanceof MockInstance3);
    assertEquals(mockInstance3, fetchedInstance);
  }

  @Test
  public void test_should_return_all_providers_of_the_given_spi() {
    // setup:
    MockInstance1 mockInstance1 = new MockInstance1();
    MockInstance2 mockInstance2 = new MockInstance2();
    MockInstance3 mockInstance3 = new MockInstance3();

    ContextMock ctx = new ContextMock(
        mockInstance1,
        mockInstance2,
        mockInstance3
    );

    List<Instance> fetchedInstances;

    when:
    fetchedInstances = Instance.providersOf(Instance.class);

    then:
    assertNotNull(fetchedInstances);
    assertTrue(fetchedInstances.size() == 3);
    assertTrue(
        fetchedInstances.containsAll(List.of(mockInstance1, mockInstance2, mockInstance3))
    );
  }
}
