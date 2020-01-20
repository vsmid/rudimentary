package hr.yeti.rudimentary.server.i18n;

import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.context.spi.Instance;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MultiResourceBundle extends ResourceBundle implements Instance {

    private List<ResourceBundle> delegates;

    @Override
    protected Object handleGetObject(String key) {
        Optional<Object> firstPropertyValue = this.delegates.stream()
            .filter(delegate -> Objects.nonNull(delegate) && delegate.containsKey(key))
            .map(delegate -> delegate.getObject(key))
            .findFirst();

        return firstPropertyValue.isPresent() ? firstPropertyValue.get() : null;
    }
    
    @Override
    public Enumeration<String> getKeys() {
        List<String> keys = this.delegates.stream()
            .filter(Objects::nonNull)
            .flatMap(delegate -> Collections.list(delegate.getKeys()).stream())
            .collect(Collectors.toList());

        return Collections.enumeration(keys);
    }

    @Override
    public void initialize() {
        ConfigProperty locale = new ConfigProperty("i18n.locale");
        ConfigProperty bundles = new ConfigProperty("i18n.bundles");

        delegates = new ArrayList<>();

        Set<String> bundlesList = new HashSet<>();
        bundlesList.addAll(List.of(bundles.asArray()));
        bundlesList.add("classpath:validation");

        bundlesList.forEach((bundle) -> {
            ResourceBundle resourceBundle = null;
            if (bundle.startsWith("classpath:")) {
                String resolvedBundle = bundle.split(":")[1];
                resourceBundle = ResourceBundle.getBundle(resolvedBundle, Locale.forLanguageTag(locale.value()));
            } else {
                try {
                    Path bundlePath = Path.of(bundle);
                    URL[] urls = { bundlePath.getParent().toUri().toURL() };
                    ClassLoader loader = new URLClassLoader(urls);
                    resourceBundle = ResourceBundle.getBundle(bundlePath.getFileName().toString(), Locale.forLanguageTag(locale.value()), loader);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(MultiResourceBundle.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IllegalStateException(ex.getMessage(), ex);
                }
            }
            if (Objects.nonNull(resourceBundle)) {
                delegates.add(resourceBundle);
            }
        });

    }
}
