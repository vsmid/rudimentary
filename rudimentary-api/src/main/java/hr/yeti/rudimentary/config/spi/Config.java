package hr.yeti.rudimentary.config.spi;

import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.context.spi.Context;
import hr.yeti.rudimentary.context.spi.Instance;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

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
 * Configuration is loaded honouring priority loading mechanism. This means that property value is
 * resolved based on the following order(top to bottom, top has the highest priority):
 * <ul>
 * <li>system property.</li>
 * <li>environment property.</li>
 * <li>config.properties file located in src/main/resources directory or any other kind of
 * properties loading mechanism using {@link Config#load} methods.</li>
 * <li>default value provided on the spot (e.g.
 * {@code new ConfigProperty("val", "defaultValue")}).</li>
 * </ul>
 *
 * @author vedransmid@yeti-it.hr
 */
public abstract class Config implements Instance {

  /**
   * Object holding resolved application properties.
   */
  protected final Map<String, ConfigProperty> configProperties = new ConcurrentHashMap<>();

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
          this.configProperties.put(property.getName(), property);
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
        props.forEach((k, v)
            -> this.configProperties.put(k.toString(), new ConfigProperty(k.toString(), v.toString()))
        );
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
      properties.forEach((k, v) -> this.configProperties.put(k, new ConfigProperty(k, v)));
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
          Properties temp = new Properties();
          temp.load(fis); // We should make sure that empty properties do not override default ones set in server module.
          this.load(temp);
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
          Properties temp = new Properties();
          temp.load(props);
          this.load(temp);
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
    return property(name, null);
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
    ConfigProperty configProperty = null;

    if (this.configProperties.keySet().contains(name)) {
      configProperty = this.configProperties.get(name);
    } else {
      configProperty = new ConfigProperty(name, value);
      this.configProperties.put(configProperty.getName(), configProperty);
    }

    return configProperty;
  }

  /**
   * Gets property value as a string.
   *
   * @param name The name of the property for which you want to get value.
   * @return Property value as a string.
   */
  public String value(String name) {
    if (this.configProperties.containsKey(name)) {
      return this.configProperties.get(name).value();
    }
    return null;
  }

  /**
   * Seals configuration instance making it ineligible for additional configuration property
   * addition by using any of the load methods provided.
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
    this.configProperties.clear();
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
    return this.configProperties.containsKey(property);
  }

  /**
   * Whether or not configuration instance is empty or not.
   *
   * @return true if configuration instance is empty, otherwise false.
   */
  public boolean isEmpty() {
    return this.configProperties.isEmpty();
  }

  /**
   * Convenience method to be used to access {@link Config} properties without having to initialize
   * a dedicated class field using {@link Instance#of(java.lang.Class)}. This method is usable when
   * a provider of {@link Context} is also present.
   *
   * @return An instance of Config class.
   */
  public static Config provider() {
    return Instance.of(Config.class);
  }

  /**
   * @return Full set of active configuration properties in {@link Properties} format.
   */
  public Properties getProperties() {
    return getPropertiesByPrefix(null, true);
  }

  /**
   * @param prefix Properties group name prefix.
   * @param keepPrefix Should prefix be kept as part of property name.
   *
   * @return A group of active configuration properties in {@link Properties} format filtered by
   * properties group name prefix.
   */
  public Properties getPropertiesByPrefix(String prefix, final boolean keepPrefix) {
    Properties props = new Properties();

    Stream<Map.Entry<String, ConfigProperty>> stream = this.configProperties.entrySet().stream();

    if (Objects.nonNull(prefix) && prefix.trim().length() > 0) {
      stream = stream.filter(e -> e.getKey().startsWith(prefix));
    }

    stream.forEach((prop) -> {
      String key = prop.getKey();
      if (!keepPrefix) {
        key = key.substring(prefix.length() + 1);
      }
      props.put(key, prop.getValue().toString());
    });

    return props;
  }

}
