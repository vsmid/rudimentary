package hr.yeti.rudimentary.config.spi;

import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.context.spi.Instance;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ServiceLoader;

/**
 * <pre>
 * An instance of this SPI should cache and hold application properties.
 * There should be only one Config provider per application.
 * </pre>
 * <p>
 * Since this abstract class implements {@link Instance} it means it is loaded automatically via
 * {@link ServiceLoader} on application startup. Currently, <i>rudimentary-server</i> module
 * provides Config provider so there is no need for you to add an additional one. It is registered
 * in the module's file
 * <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.config.spi.Config</i>
 *
 * Configuration is loaded honoring priority loading mechanism. This means that property value which
 * will be return is decided in the following order(top to bottom, top has the highest priority):
 * <ul>
 * <li>system property.</li>
 * <li>environment property.</li>
 * <li>config.properties file located in src/main/resources directory or any other kind of property
 * loading using {@link Config#load} methods.</li>
 * <li>default value provided on the spot (e.g.
 * {@code new ConfigProperty("val", "defaultValue")}).</li>
 * </ul>
 *
 * @author vedransmid@yeti-it.hr
 */
public abstract class Config implements Instance {

  /**
   * Object holding application properties.
   */
  protected final Properties properties = new Properties();

  /**
   * A property indicating whether current configuration instance is sealed or not. When
   * configuration instance is sealed it means no additional property can be added.
   */
  protected boolean sealed = false;

  /**
   * A method used to load properties using an array of {@link ConfigProperty}.
   *
   * @param properties An array of configuration properties to be added to configuration.
   * @return An instance of {@link Config}.
   */
  public Config load(ConfigProperty... properties) {
    if (!isSealed()) {
      if (Objects.nonNull(properties)) {
        for (ConfigProperty property : properties) {
          this.properties.put(property.getName(), property.value());
        }
      }
    }

    return this;
  }

  /**
   * A method used to load properties using an array of {@link Properties}.
   *
   * @param properties An array of configuration properties to be added to configuration.
   * @return An instance of {@link Config}.
   */
  public Config load(Properties... properties) {
    if (!isSealed()) {
      for (Properties props : properties) {
        this.properties.putAll(props);
      }
    }

    return this;
  }

  /**
   * A method used to load properties using an array of {@link Map<String, String>}.
   *
   * @param properties A map of configuration properties to be added to configuration.
   * @return An instance of {@link Config}.
   */
  public Config load(Map<String, String> properties) {
    if (!isSealed()) {
      this.properties.putAll(properties);
    }

    return this;
  }

  /**
   * A method used to load properties using an array of strings representing paths to properties
   * files.
   *
   * @param propertiesFileLocations An array of strings, each representing path to the properties
   * file to be added to configuration.
   * @return An instance of {@link Config}.
   */
  public Config load(String... propertiesFileLocations) {
    if (!isSealed()) {
      for (String propertiesFileLocation : propertiesFileLocations) {
        try (FileInputStream fis = new FileInputStream(propertiesFileLocation)) {
          this.properties.load(fis); // We should make sure that empty properties do not override default ones set in server module.
        } catch (IOException e) {
          throw new RuntimeException(
              "Failed to load configuration from " + propertiesFileLocation, e
          );
        }
      }
    }

    return this;
  }

  /**
   * A method used to load properties using an array of {@link InputStream}.
   *
   * @param properties An array of configuration properties loaded from {@link InputStream} to be
   * added to configuration.
   * @return An instance of {@link Config}.
   */
  public Config load(InputStream... properties) {
    if (!isSealed()) {
      try {
        for (InputStream props : properties) {
          this.properties.load(props);
        }
      } catch (IOException e) {
        throw new RuntimeException("Failed to load configuration", e);
      }
    }

    return this;
  }

  /**
   * Gets property value.
   *
   * @param name The name of the property for which you want to get value.
   * @return Configuration property value in the {@link ConfigProperty} form.
   */
  public ConfigProperty property(String name) {
    return new ConfigProperty(name);
  }

  /**
   * Gets property value but also sets the default value to be returned in case of the property not
   * being set.
   *
   * @param name The name of the property for which you want to get value.
   * @param value The default value to be returned in case of the property not being not set.
   * @return Configuration property value in the {@link ConfigProperty} form.
   */
  public ConfigProperty property(String name, String value) {
    return new ConfigProperty(name, value);
  }

  /**
   * Gets property value as a string.
   *
   * @param name The name of the property for which you want to get value.
   * @return Property value as a string.
   */
  public String value(String name) {
    return this.properties.getProperty(name);
  }

  /**
   * Seals configuration instance making it ineligible for additional configuration property
   * addition.
   */
  public void seal() {
    this.sealed = true;
  }

  /**
   * Gets instance sealed status.
   *
   * @return true is configuration instance is sealed, otherwise false.
   */
  public boolean isSealed() {
    return this.sealed;
  }

  /**
   * Destroys configuration instance by clearing properties object holder making it eligible for
   * configuration properties addition.
   */
  @Override
  public void destroy() {
    this.properties.clear();
    this.sealed = false;
  }

  /**
   * Whether or not configuration instance contains property or not.
   *
   * @param property The name of the property you wish to check whether or not is contained withing
   * configuration instance.
   *
   * @return true if configuration instance contains property, otherwise false.
   */
  public boolean contains(String property) {
    return this.properties.containsKey(property);
  }

  /**
   * Whether or not configuration instance is empty or not.
   *
   * @return true if configuration instance is empty, otherwise false.
   */
  public boolean isEmpty() {
    return this.properties.isEmpty();
  }

  /**
   * Convenience method to be used to access {@link Config} properties without having to initialize
   * a dedicated class field using {@link Instance#of(java.lang.Class)}.
   *
   * @return
   */
  public static Config provider() {
    return Instance.of(Config.class);
  }

  /**
   * @return Full set of configuration properties in raw format.
   */
  public Properties rawProperties() {
    Properties props = new Properties();
    propertiesg.forEach((key, value) -> {
      props.put(key, value);
    });
    return props;
  }
}
