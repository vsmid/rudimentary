package hr.yeti.rudimentary.test;

import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Context;
import hr.yeti.rudimentary.context.spi.Instance;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ContextMock extends Context {

  public ContextMock(Instance... instances) {
    this.setup(null, instances);
  }

  public ContextMock(Config config, Instance... instances) {
    this.setup(config, instances);
  }

  private void setup(Config config, Instance... instances) {
    // Used to test destroy method.
    super.destroy();

    if (Objects.nonNull(config)) {
      super.initializeInstance(config);
      super.add(config);
    }

    for (Instance instance : instances) {
      super.add(instance);
    }

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
