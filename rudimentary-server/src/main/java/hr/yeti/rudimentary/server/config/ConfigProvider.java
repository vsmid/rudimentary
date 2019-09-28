package hr.yeti.rudimentary.server.config;

import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.server.resources.ClasspathResource;

public class ConfigProvider extends Config {

  @Override
  public void initialize() {
    load(
        new ClasspathResource("default-config.properties").get(),
        new ClasspathResource("config.properties").get()
    );
    seal();
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
