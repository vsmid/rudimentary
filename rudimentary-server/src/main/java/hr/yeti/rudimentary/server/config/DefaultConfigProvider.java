package hr.yeti.rudimentary.server.config;

import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.server.resources.ClasspathResource;
import java.util.ServiceLoader;

public final class DefaultConfigProvider extends Config {

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
    return false;
  }

  @Override
  public boolean conditional() {
    return ServiceLoader.load(Config.class).stream().count() == 1;
  }

}
