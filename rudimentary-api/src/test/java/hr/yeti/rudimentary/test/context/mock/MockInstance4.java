package hr.yeti.rudimentary.test.context.mock;

import hr.yeti.rudimentary.context.spi.Instance;

public class MockInstance4 implements Instance {

  @Override
  public boolean primary() {
    return false;
  }

}
