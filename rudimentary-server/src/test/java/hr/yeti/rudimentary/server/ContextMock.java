package hr.yeti.rudimentary.server;

import hr.yeti.rudimentary.context.spi.Context;
import hr.yeti.rudimentary.context.spi.Instance;

public class ContextMock extends Context {

  public ContextMock(Instance... instances) {
    // Used to test destroy method.
    super.destroy();

    for (Instance instance : instances) {
      // Used to test initialize method.
      instance.initialize();

      // Used to test both protected methods, Context#add and Context#setInitialized.
      super.add(instance);
      super.setInitialized(instance);
    }
  }

  // Method used to test protected Context#isInstanceInitialized method.
  public boolean initialized(Class<?> clazz) {
    return super.isInstanceInitialized(clazz);
  }

}
