package hr.yeti.rudimentary.test;

import hr.yeti.rudimentary.config.spi.Config;

/**
 * Class used for testing purposes to mock configuration. It is used by {@link ContextMock}.
 *
 * @author vedransmid@yeti-it.hr
 */
public final class ConfigMock extends Config {

  public ConfigMock() {
    super.destroy();
  }

}
