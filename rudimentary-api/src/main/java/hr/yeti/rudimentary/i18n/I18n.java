package hr.yeti.rudimentary.i18n;

import hr.yeti.rudimentary.config.ConfigProperty;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class used for simple retrieving of localized messaged. This depends on two configuration
 * properties:
 *
 * <pre>
 * i18n.locale=en # Locale language/country combination e.g. en or en_US. If no locale is set, system default Locale is used
 * i18n.bundles=classpath:messages # Comma separated list of .properties files without .properties extension. For files on classpath use prefix classpath:, e.g. classpath:myMessages, otherwise use file absolute path without locale and .properties prefix (/Users/dummy/messages)
 * </pre>
 *
 * <pre>
 * For the above properties, a file with name messages_en.properties should be placed in
 * src/main/resources directory.
 *
 * Supported message format is the one used in {@link MessageFormat}.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public class I18n {

    private static Map<Locale, MultiResourceBundle> multiResourceBundleMap = new ConcurrentHashMap<>();

    /**
     * Get text message for the default locale.
     *
     * @param key Property key under which value is stored in property file.
     * @param params Parameters passed to {@link MessageFormat#format} method.
     * @return Formatted text message.
     */
    public static String text(String key, Object... params) {
        return text(key, resolveDefaultLocale(), params);
    }

    /**
     * Get text message for the given locale.
     *
     * @param key Property key under which value is stored in property file.
     * @param locale Locale
     * @param params Parameters passed to {@link MessageFormat#format} method.
     * @return Formatted text message.
     */
    public static String text(String key, Locale locale, Object... params) {
        ResourceBundle multiResourceBundle = acquireMultiResourceBundle(locale);
        String value = multiResourceBundle.getString(key);
        return MessageFormat.format(value, params);
    }

    private static MultiResourceBundle acquireMultiResourceBundle(Locale locale) {
        if (!multiResourceBundleMap.containsKey(locale)) {
            MultiResourceBundle bundle = new MultiResourceBundle(locale);
            multiResourceBundleMap.put(locale, bundle);
        }
        return multiResourceBundleMap.get(locale);
    }

    public static Locale resolveDefaultLocale() {
        ConfigProperty localeProperty = new ConfigProperty("i18n.locale");
        if (localeProperty.isNull() || localeProperty.isBlank(true)) {
            return Locale.getDefault();
        }
        return Locale.forLanguageTag(localeProperty.value());
    }
}
