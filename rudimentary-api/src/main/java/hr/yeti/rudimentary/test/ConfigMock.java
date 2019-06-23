package hr.yeti.rudimentary.test;

import hr.yeti.rudimentary.config.spi.Config;

public final class ConfigMock extends Config {

  public ConfigMock() {
    super.destroy();
  }

}
