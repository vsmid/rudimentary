package hr.yeti.rudimentary.test.config.mock;

import hr.yeti.rudimentary.config.spi.Config;

public class ConfigMock extends Config {

  public ConfigMock() {
    super.destroy();
  }

}
