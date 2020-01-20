package hr.yeti.rudimentary.i18n;

import hr.yeti.rudimentary.context.spi.Instance;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Class used for simple retrieving of localized messaged. This depends on two configuration
 * properties:
 *
 * <pre>
 * i18n.locale=en # Locale language/country combination e.g. en or en_US
 * i18n.bundles=classpath:messages # Comma separated list of .properties files without .properties extension. For files on classpath use prefix classpath:, e.g. classpath:myMessages, otherwise use file absolute path without locale nad .properties prefix (/Users/dummy/messages)
 * </pre>
 *
 * <pre>
 * For the above properties, a file with name messages_en.properties should be placed in
 * src/main/resources directory.
 *
 * Supported message format is the one used in {@link MessageFormat}.
 *</pre>
 * @author vedransmid@yeti-it.hr
 */
public class I18n {

    /**
     * Get localized text message.
     *
     * @param key Property key under which value is stored in property file.
     * @param params Parameters passed to {@link MessageFormat#format} method.
     * @return Formatted text message.
     */
    public static String text(String key, Object... params) {
        ResourceBundle multiResourceBundle = Instance.of(ResourceBundle.class);
        String value = multiResourceBundle.getString(key);
        return MessageFormat.format(value, params);
    }

    public static String text(String key, Locale locale, Object... params) {
        ResourceBundle multiResourceBundle = Instance.of(ResourceBundle.class);
        String value = multiResourceBundle.getString(key);
        return MessageFormat.format(value, params);
    }
}
