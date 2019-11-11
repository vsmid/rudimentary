package hr.yeti.rudimentary.server.config;

import hr.yeti.rudimentary.config.ConfigException;
import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.server.resources.ClasspathResource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ServiceLoader;

public final class DefaultConfigProvider extends Config {

  @Override
  public void initialize() {
    try (
        InputStream defaultConfig = new ClasspathResource("default-config.properties").get();
        InputStream config = new ClasspathResource("config.properties").get()) {
      load(defaultConfig, config);
    } catch (IOException ex) {
      throw new ConfigException(ex);
    }
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
