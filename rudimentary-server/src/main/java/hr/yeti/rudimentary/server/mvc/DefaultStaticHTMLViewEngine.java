package hr.yeti.rudimentary.server.mvc;

import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.mvc.ViewEngineException;
import hr.yeti.rudimentary.mvc.spi.ViewEngine;
import hr.yeti.rudimentary.server.resources.ClasspathResource;
import java.io.IOException;
import java.io.InputStream;

public class DefaultStaticHTMLViewEngine implements ViewEngine {

    private ConfigProperty templatesDir = new ConfigProperty("mvc.templatesDir");
    private ConfigProperty staticResourcesDir = new ConfigProperty("mvc.staticResourcesDir");

    @Override
    public String render(View view) throws ViewEngineException {
        try {
            InputStream html = new ClasspathResource(getTemplatesDirectory() + "/" + view.getTemplatePath()).get();
            return new String(html.readAllBytes());
        } catch (IOException ex) {
            logger().log(System.Logger.Level.ERROR, ex);
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
    public boolean primary() {
        return false;
    }

}
