package hr.yeti.rudimentary.server.config;

import hr.yeti.rudimentary.config.spi.Config;

public class ConfigProvider extends Config {

  @Override
  public void initialize() {
    load(
        ConfigProvider.class.getClassLoader().getResourceAsStream("default-config.properties"),
        ConfigProvider.class.getClassLoader().getResourceAsStream("config.properties")
    );
  }

  @Override
  public void destroy() {
    super.destroy();
  }

  @Override
  public boolean primary() {
    return true;
  }

}
