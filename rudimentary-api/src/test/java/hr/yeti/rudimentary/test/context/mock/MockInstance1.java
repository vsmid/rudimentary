package hr.yeti.rudimentary.test.context.mock;

import hr.yeti.rudimentary.context.spi.Instance;

public class MockInstance1 implements Instance {

  private String value;

  @Override
  public void initialize() {
    this.value = "10";
  }

  @Override
  public boolean primary() {
    return true;
  }

  public String getValue() {
    return value;
  }

}
