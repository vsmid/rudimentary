package hr.yeti.rudimentary.config;

import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.http.value.Transformable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <pre>
 * An instance of configuration property. A good usage of this would be if you are referencing the
 * same property in the same class multiple times. This class gives you an ability to convert the
 * same property to different type on the fly.
 *
 * {@code
 * public class Service {
 *
 *  private ConfigProperty serverPort = new ConfigProperty("server.port");
 *
 *  public void print() {
 *    System.out.print(serverPort.value()); // Prints string value
 *    System.out.print(serverPort.asInt()); // Prints int value
 *    ...
 *  }
 * }
 * }
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public class ConfigProperty extends Transformable {

    /**
     * Holds the name of the property.
     */
    private final String name;

    /**
     * Constructor used to set property value without setting the default value if the configuration property is missing.
     *
     * @param name The name of the property.
     */
    public ConfigProperty(String name) {
        this.name = name;
        value = setValue(null);
    }

    /**
     * Constructor used to set property value by also providing the default value if the configuration property is missing.
     *
     * @param name The name of the property.
     * @param defaultValue The default value of the property if the configuration property is missing.
     */
    public ConfigProperty(String name, String defaultValue) {
        this.name = name;
        value = setValue(defaultValue);
    }

    /**
     * Sets the property value honoring priority loading mechanism.
     *
     * @see Config for more info about priority loading mechanism.
     *
     * @param value A default value to be used if property is not set.
     * @return The value of the property.
     */
    private String setValue(String value) {
        String property = System.getProperty(name);

        if (Objects.isNull(property)) {
            property = System.getenv(name);
        }

        if (Objects.isNull(property)) {
            if (Objects.nonNull(Config.provider())) {
                property = Config.provider().value(name);
            }
        }

        if (Objects.isNull(property)) {
            property = value;
        }

        return property;
    }

    /**
     * Gets property name.
     *
     * @return Property name as string.
     */
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConfigProperty other = (ConfigProperty) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(value, other.value)) {
            return false;
        }
        return true;
    }

}
