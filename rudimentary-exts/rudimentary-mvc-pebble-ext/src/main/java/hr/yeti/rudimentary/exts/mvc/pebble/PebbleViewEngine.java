package hr.yeti.rudimentary.exts.mvc.pebble;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.cache.tag.CaffeineTagCache;
import com.mitchellbosecke.pebble.cache.template.CaffeineTemplateCache;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.mvc.ViewEngineException;
import hr.yeti.rudimentary.mvc.spi.ViewEngine;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class PebbleViewEngine implements ViewEngine {

    private ConfigProperty templatesDir = new ConfigProperty("mvc.templatesDir");
    private ConfigProperty staticResourcesDir = new ConfigProperty("mvc.staticResourcesDir");

    private ConfigProperty cacheActive = new ConfigProperty("mvc.ext.pebble.cacheActive", "true");
    private ConfigProperty strictVariables = new ConfigProperty("mvc.ext.pebble.strictVariables", "false");
    private ConfigProperty allowUnsafeMethods = new ConfigProperty("mvc.ext.pebble.allowUnsafeMethods", "false");
    private ConfigProperty literalDecimalTreatedAsInteger = new ConfigProperty("mvc.ext.pebble.literalDecimalTreatedAsInteger", "false");
    private ConfigProperty greedyMatchMethod = new ConfigProperty("mvc.ext.pebble.greedyMatchMethod", "false");
    private ConfigProperty executorServiceClass = new ConfigProperty("mvc.ext.pebble.executorServiceClass");
    private ConfigProperty defaultLocale = new ConfigProperty("mvc.ext.pebble.defaultLocale");
    private ConfigProperty caffeineTemplateCache = new ConfigProperty("mvc.ext.pebble.caffeineTemplateCache", "false");
    private ConfigProperty caffeineTagCache = new ConfigProperty("mvc.ext.pebble.caffeineTagCache", "false");

    private PebbleEngine engine;

    @Override
    public String render(View view) throws ViewEngineException {
        try {
            PebbleTemplate compiledTemplate = engine.getTemplate(view.getTemplatePath());
            Writer writer = new StringWriter();
            compiledTemplate.evaluate(writer, view.getContext());
            return writer.toString();
        } catch (LoaderException | IOException ex) {
            logger().log(System.Logger.Level.ERROR, ex);
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

        PebbleEngine.Builder engineBuilder = new PebbleEngine.Builder()
            .cacheActive(cacheActive.asBoolean())
            .strictVariables(strictVariables.asBoolean())
            .allowUnsafeMethods(allowUnsafeMethods.asBoolean())
            .literalDecimalTreatedAsInteger(literalDecimalTreatedAsInteger.asBoolean())
            .greedyMatchMethod(greedyMatchMethod.asBoolean())
            .loader(loader);

        if (Objects.nonNull(executorServiceClass.value())) {
            try {
                engineBuilder.executorService(
                    (ExecutorService) Instance.of(Class.forName(executorServiceClass.value()))
                );
            } catch (ClassNotFoundException ex) {
                throw new ViewEngineException("Could not set executor service based on class " + executorServiceClass.value() + ".");
            }
        }

        if (Objects.nonNull(defaultLocale.value())) {
            engineBuilder.defaultLocale(Locale.forLanguageTag(defaultLocale.value()));
        }

        if (Objects.nonNull(caffeineTemplateCache.value())) {
            if (caffeineTemplateCache.asBoolean()) {
                engineBuilder.templateCache(new CaffeineTemplateCache());
            }
        }

        if (Objects.nonNull(caffeineTagCache.value())) {
            if (caffeineTagCache.asBoolean()) {
                engineBuilder.tagCache(new CaffeineTagCache());
            }
        }

        this.engine = engineBuilder.build();
    }

    @Override
    public boolean primary() {
        return true;
    }

}
