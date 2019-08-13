package hr.yeti.rudimentary.test;

import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Context;
import hr.yeti.rudimentary.context.spi.Instance;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used for testing purposes to create mocked context on the fly.
 *
 * @author vedransmid@yeti-it.hr
 */
public final class ContextMock extends Context {

  private List<Instance> instances = new ArrayList<>();

  /**
   * Create context from instances. Use this constructor if instances do not depend on some
   * configuration properties.
   *
   * @param instances An instance of class implementing {@link Instance}.
   */
  public ContextMock(Instance... instances) {
    this.instances.addAll(Arrays.asList(instances));
    initialize();
  }

  /**
   * Create context from classes.Use this constructor if instances do depend on some configuration
   * properties.
   *
   * @param properties Configuration properties.
   * @param instances An instance of class implementing {@link Instance}.
   */
  public ContextMock(Map<String, String> properties, Class<? extends Instance>... instances) {
    Config config = new ConfigMock().load(properties);
    config.seal();

    if (Objects.nonNull(config)) {
      super.initializeInstance(config);
      super.add(config);
    }

    for (Class<? extends Instance> instance : instances) {
      try {
        this.instances.add(instance.getDeclaredConstructor().newInstance());
      } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        Logger.getLogger(ContextMock.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    initialize();
  }

  @Override
  public void initialize() {
    super.destroy();
    instances.forEach(super::add);

    super.buildInstanceDependenciesGraph();
    instanceDependencyGraph.keySet().forEach((instance) -> {
      super.checkForCircularDependencies(instance, null);
    });

    getContext().values()
        .forEach((instance) -> {
          super.initializeInstance(instance);
        });
  }

  // Method used to test protected Context#isInstanceInitialized method.
  public boolean initialized(Class<?> clazz) {
    return super.isInstanceInitialized(clazz);
  }

  // Method used to test instanceDependencyGraph map.
  public Map<String, List<String>> getInstanceDependencyGraph() {
    return instanceDependencyGraph;
  }

}
