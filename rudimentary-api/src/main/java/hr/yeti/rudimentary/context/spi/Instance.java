package hr.yeti.rudimentary.context.spi;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <pre>
 * Implement this class to gain automatic service loading feature.
 * Upon application/service start all classes implementing this interface will be cached in memory.
 * To register desired implementation provider you must also write fully qualified provider class
 * name to <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.context.spi.Instance</i>
 * file.
 *
 * e.g. Let's say we have a class declared as:
 *
 * {@code
 * package hr.yeti.mypackage;
 *
 * public class Service implements Instance {
 *  // TODO Implement interface methods...
 * }}
 * You would then add a new entry <i>hr.yet.mypackage.Service</i> to
 * <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.context.spi.Instance</i> file.
 * You can create this file in this exact location if it doesn't exist yet.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public interface Instance {

  /**
   * <pre>
   * To access any given instance loaded into {@link Context} use this static method.
   * Here you can also use a SPI reference class. However, SPI referenced class can have
   * multiple providers and this method returns only one provider.
   * Provider that will be returned is the one having {@link Instance#primary()} set to true.
   * If there are no primary providers or there are multiple providers a first
   * provider found is returned.
   * The order in which providers are found is the order in which you register them in
   * META-INF/service files.
   *
   * An example of SPI referenced class would be {@link Config}.
   *
   * It is important to note here that some special SPI's such as {@link Config} will also have
   * their own static
   * access to provider's instance aside from the regular one stated above. That is to remove the
   * need of declaring fields in classes in order to access them.
   * It is just a convenience static method to make things more simple and on-the-fly.
   * </pre>
   *
   * @param <T> Inferred class type generics.
   * @param clazz Class type to be returned.
   * @return An instance of given @param class.
   */
  static <T> T of(Class<T> clazz) {

    T instance = (T) Context.getContext().get(clazz.getSimpleName().toLowerCase());

    if (Objects.isNull(instance)) {
      List<T> providersOf = providersOf(clazz);

      if (!providersOf.isEmpty()) {
        Optional<T> provider = providersOf.stream()
            .filter((i) -> {
              return ((Instance) i).primary();
            })
            .findFirst();

        if (provider.isPresent()) {
          instance = provider.get();
        } else {
          instance = providersOf.get(0);
        }

      }
    }

    // TODO throw exception on null instead null?
    return instance;
  }

  /**
   * To access a list of all providers for a given SPI use this method.
   *
   * @param <T> Inferred class type generics.
   * @param clazz Class type to be returned.
   * @return A list of instances of given @param class type.
   */
  static <T> List<T> providersOf(Class<T> clazz) {
    return (List<T>) Context.getContext()
        .values()
        .stream()
        .filter(instance -> clazz.isAssignableFrom(instance.getClass()))
        .collect(Collectors.toList());
  }

  /**
   * <pre>
   * This is the id value under which an instance of this class is stored inside Context map.
   * An instance if retrieved from Context using this id value.
   *
   * For now, it is set to lower case simepl class name by default.
   * </pre>
   *
   * @return An id of the instance.
   */
  default String id() {
    return this.getClass().getSimpleName().toLowerCase();
  }

  /**
   * <pre>
   * This is a simple mechanism to ensure that instances required for
   * initialization of this instance are initialized before this instance.
   * </pre>
   *
   * @return A list of classes that this instance depends on.
   */
  default Class[] dependsOn() {
    return new Class[]{};
  }

  /**
   * <pre>
   * Implement this method when you want to perform any kind of setup, initialization etc.
   * immediately after this instance constructor method has been called.
   * </pre>
   */
  default void initialize() {

  }

  /**
   * <pre>
   * Implement this method when you want to perform any kind of action when the context is being
   * shut down.
   * This method is not intended to be used except for internal usage.
   * </pre>
   */
  default void destroy() {
    // TODO Shutdown is not yet implemented.
  }

  /**
   * <pre>
   * An indicator that this instance is to be returned when there are multiple providers
   * of the same SPI and the above {@link Instance#of(java.lang.Class)} method is called.
   * If there are multiple primary providers, the first one encountered is returned. This however is a bad
   * practice, there should only be one primary provider. Use this with caution.
   * </pre>
   *
   * @return true if this instance is primary, otherwise false by default.
   */
  default boolean primary() {
    return false;
  }

  /**
   * Indicates whether an instance of this class should be created and put into application/service context or not.
   *
   * @return True if an instance should be created, otherwise false.
   */
  default boolean conditional() {
    return true;
  }
}
