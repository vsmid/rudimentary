package hr.yeti.rudimentary.context.spi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * <pre>
 * An instance of this SPI should cache and hold application instances loaded via
 * {@link ServiceLoader}.
 * There should be only one Context provider per application.
 * </pre>
 * <p>
 * Since this abstract class implements {@link Instance} it means it is loaded automatically via
 * {@link ServiceLoader} on application startup. An instance of this class is not intended to be
 * used except internally. Currently, <i>rudimentary-server</i> module provides Context provider so
 * there is no need for you to add an additional one. It is registered in the module's file
 * <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.context.spi.Context</i>
 *
 * @author vedransmid@yeti-it.hr
 */
public abstract class Context implements Instance {

  /**
   * <pre>
   * Map intended only for reading.
   * Contains all initialized instances loaded via {@link ServiceLoader}.
   * </pre>
   */
  protected static final Map<String, Instance> CONTEXT = new LinkedHashMap<>();

  /**
   * Map holding already initialized SPI providers.
   */
  protected static final List<String> INITIALIZED_INSTANCES = new ArrayList<>();

  /**
   * <pre>
   * Gets all instances loaded via service loader.
   * Even though this method can be used to get any given instance it is discouraged.
   * </pre>
   * <p>
   * It is better and recommended to use {@link Instance#of(java.lang.Class)}.
   *
   * @return An active application context.
   */
  public static Map<String, Instance> getContext() {
    return CONTEXT;
  }

  /**
   * <pre>
   * Gets all instances canonical class names that are already
   * initialized and put into {@link Context}.
   * </pre>
   *
   * @return A list of canonical class name string values representing already initialized
   * instances.
   */
  public static List<String> getInitializedInstances() {
    return INITIALIZED_INSTANCES;
  }

  /**
   * <pre>
   * Method called on application shutdown.
   * Clears internal instances cache.
   * </pre>
   */
  @Override
  public void destroy() {
    CONTEXT.clear();
    INITIALIZED_INSTANCES.clear();
  }

  /**
   * <pre>
   * Convenience method to add instance to a context.
   * Intended to be used only in classes extending this class.
   * </pre>
   *
   * @param instance An instance to be added to context.
   */
  protected void add(Instance instance) {
    if (instance.conditional()) {
      CONTEXT.put(instance.getClass().getCanonicalName(), instance);
    }
  }

  /**
   * <pre>
   * Convenience method to mark given instance as initialized.
   * Intended to be used only in classes extending this class.
   * </pre>
   *
   * @param instance
   */
  protected void setInitialized(Instance instance) {
    INITIALIZED_INSTANCES.add(instance.getClass().getCanonicalName());
  }

  /**
   * <pre>
   * Convenience method to check whether an instance has been initialized.
   * This method is used during context loading process to avoid multiple initialize method calls.
   * Intended to be used only in classes extending this class.
   * </pre>
   *
   * @param clazz Class of the instance.
   * @return true if an instance of the parameter clazz has been initialized, otherwise false.
   */
  protected boolean isInstanceInitialized(Class<?> clazz) {
    return INITIALIZED_INSTANCES.contains(clazz.getCanonicalName());
  }

}
