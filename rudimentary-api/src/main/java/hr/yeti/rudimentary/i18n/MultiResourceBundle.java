package hr.yeti.rudimentary.i18n;

import hr.yeti.rudimentary.config.ConfigProperty;
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

/**
 * Custom ResourceBundle implementation which loads multiple bundles.
 *
 * @author vedransmid@yeti-it.hr
 */
public class MultiResourceBundle extends ResourceBundle {

    private List<ResourceBundle> delegates;

    public MultiResourceBundle() {
        set(I18n.resolveDefaultLocale());
    }

    public MultiResourceBundle(Locale locale) {
        if (Objects.isNull(locale)) {
            locale = I18n.resolveDefaultLocale();
        }
        set(locale);
    }

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

    private void set(Locale locale) {
        ConfigProperty bundles = new ConfigProperty("i18n.bundles");

        delegates = new ArrayList<>();

        Set<String> bundlesList = new HashSet<>();
        bundlesList.addAll(List.of(bundles.asArray()));
        bundlesList.add("classpath:validation");

        bundlesList.forEach((bundle) -> {
            ResourceBundle resourceBundle = null;
            if (bundle.startsWith("classpath:")) {
                String resolvedBundle = bundle.split(":")[1];
                resourceBundle = ResourceBundle.getBundle(resolvedBundle, locale);
            } else {
                try {
                    Path bundlePath = Path.of(bundle);
                    URL[] urls = { bundlePath.getParent().toUri().toURL() };
                    ClassLoader loader = new URLClassLoader(urls);
                    resourceBundle = ResourceBundle.getBundle(bundlePath.getFileName().toString(), locale, loader);
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
