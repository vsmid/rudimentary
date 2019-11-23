package hr.yeti.rudimentary.exts.mvc.pebble;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.mvc.ViewEngineException;
import hr.yeti.rudimentary.mvc.spi.ViewEngine;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PebbleViewEngine implements ViewEngine {

    private static final Logger LOGGER = Logger.getLogger(PebbleViewEngine.class.getName());

    private ConfigProperty templatesDir = new ConfigProperty("mvc.templatesDir");
    private ConfigProperty staticResourcesDir = new ConfigProperty("mvc.staticResourcesDir");

    private PebbleEngine engine;

    @Override
    public String render(View view) throws ViewEngineException {
        try {
            PebbleTemplate compiledTemplate = engine.getTemplate(view.getTemplatePath());
            Writer writer = new StringWriter();
            compiledTemplate.evaluate(writer, view.getContext());
            return writer.toString();
        } catch (LoaderException | IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage());
            throw new ViewEngineException("Could not render view.");
        }
    }

    @Override
    public String getTemplatesDirectory() {
        return templatesDir.toString();
    }

    @Override
    public String getStaticResourcesDirectory() {
        return staticResourcesDir.value();
    }

    @Override
    public void initialize() {
        ClasspathLoader loader = new ClasspathLoader();

        loader.setPrefix(templatesDir.value());

        engine = new PebbleEngine.Builder()
                .loader(loader)
                .build();
    }

}
