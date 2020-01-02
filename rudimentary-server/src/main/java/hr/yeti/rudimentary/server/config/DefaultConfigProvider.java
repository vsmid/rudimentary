package hr.yeti.rudimentary.server.config;

import hr.yeti.rudimentary.config.ConfigException;
import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.server.resources.ClasspathResource;
import java.io.IOException;
import java.io.InputStream;

public final class DefaultConfigProvider extends Config {

    @Override
    public void initialize() {
        super.initialize();

        try (
            InputStream config = new ClasspathResource("config.properties").get()) {
            load(config);
        } catch (IOException ex) {
            throw new ConfigException(ex);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public boolean primary() {
        return false;
    }

}
