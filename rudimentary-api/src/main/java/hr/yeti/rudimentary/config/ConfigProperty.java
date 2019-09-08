package hr.yeti.rudimentary.config;

import hr.yeti.rudimentary.config.spi.Config;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
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
public class ConfigProperty {

  /**
   * Holds the name of the property.
   */
  private final String name;

  /**
   * Holds property value.
   */
  private final String value;

  /**
   * Constructor used to set property value without setting the default value if the configuration
   * property is missing.
   *
   * @param name The name of the property.
   */
  public ConfigProperty(String name) {
    this.name = name;
    this.value = setValue(null);
  }

  /**
   * Constructor used to set property value by also providing the default value if the configuration
   * property is missing.
   *
   * @param name The name of the property.
   * @param defaultValue The default value of the property if the configuration property is missing.
   */
  public ConfigProperty(String name, String defaultValue) {
    this.name = name;
    this.value = setValue(defaultValue);
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
   * Gets property value.
   *
   * @return Configuration property value as string.
   */
  public String value() {
    return this.value;
  }

  /**
   * Gets property value as integer.
   *
   * @return Configuration property value as integer.
   */
  public Integer asInt() {
    return Integer.valueOf(this.value);
  }

  /**
   * Gets property value as long.
   *
   * @return Configuration property value as long.
   */
  public Long asLong() {
    return Long.valueOf(this.value);
  }

  /**
   * Gets property value as boolean.
   *
   * @return Configuration property value as boolean.
   */
  public Boolean asBoolean() {
    return Boolean.valueOf(this.value);
  }

  /**
   * <pre>
   * Gets property value as array of strings.
   * It is mandatory that property value is given as comma separated string values.
   *
   * e.g. cities=Zagreb, Podlonk, Zali Log.
   *
   * </pre>
   *
   * @return Configuration property value as array of string.
   */
  public String[] asArray() {
    return Stream.of(this.value.split(","))
        .map(String::trim)
        .filter(val -> !val.isEmpty())
        .toArray(String[]::new);
  }

  /**
   * Gets property value as {@link URI}.
   *
   * @return Configuration property value as {@link URI}.
   */
  public URI asURI() {
    return URI.create(this.value);
  }

  /**
   * Gets property value as {@link URL}.
   *
   * @return Configuration property value as {@link URL}.
   */
  public URL asURL() {
    try {
      return new URL(this.value);
    } catch (MalformedURLException ex) {
      throw new ConfigException(ex);
    }
  }

  /**
   * Gets property value as string.
   *
   * @return Configuration property value as string.
   */
  @Override
  public String toString() {
    return this.value;
  }

  /**
   * Gets property name.
   *
   * @return Property name as string.
   */
  public String getName() {
    return name;
  }

}
