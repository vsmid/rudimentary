package hr.yeti.rudimentary.server.mvc;

import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.mvc.ViewEngineException;
import hr.yeti.rudimentary.mvc.spi.ViewEngine;
import hr.yeti.rudimentary.server.resources.ClasspathResource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultStaticHTMLViewEngine implements ViewEngine {

    private static final Logger LOGGER = Logger.getLogger(DefaultStaticHTMLViewEngine.class.getName());

    private ConfigProperty templatesDir = new ConfigProperty("mvc.templatesDir");
    private ConfigProperty staticResourcesDir = new ConfigProperty("mvc.staticResourcesDir");

    @Override
    public String render(View view) throws ViewEngineException {
        try {
            InputStream html = new ClasspathResource(getTemplatesDirectory() + "/" + view.getTemplatePath()).get();
            return new String(html.readAllBytes());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new ViewEngineException("Could not resolve view.");
        }
    }

    @Override
    public String getTemplatesDirectory() {
        return templatesDir.value();
    }

    @Override
    public String getStaticResourcesDirectory() {
        return staticResourcesDir.value();
    }

    @Override
    public boolean conditional() {
        // Only load when no other view engine is provided.
        return ServiceLoader.load(ViewEngine.class).stream().count() == 1;
    }

}
