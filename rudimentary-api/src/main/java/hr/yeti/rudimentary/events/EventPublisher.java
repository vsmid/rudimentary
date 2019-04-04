package hr.yeti.rudimentary.events;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.events.spi.EventListener;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A little bit custom implementation of observer pattern.
 *
 * Since this class implements {@link Instance} it means it is loaded automatically via
 * {@link ServiceLoader} on application startup.
 *
 * In practice, the only time you could use an instance of this class if you would want to publish
 * an event. Preferred way of publishing events is shown in {@link Event}.
 *
 * <pre>
 * {@code
 *  ...
 *  Instance.of(EventPublisher.class).publish(new BlogPost("Post 2."), EventPublisher.Type.SYNC);
 *  ...
 * }
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public final class EventPublisher implements Instance {

  /**
   * Enumerated type of how event should be published. This has an effect on the way how listeners
   * process published event.
   */
  public enum Type {
    SYNC, ASYNC
  }

  /**
   * A map of all registered {@link EventListener}. One event class type can have multiple listeners
   * registered.
   */
  private static final Map<Class<? extends Event>, List<EventListener<Event>>> LISTENERS = new ConcurrentHashMap<>();

  /**
   * <pre>
   * Call this class to publish an event. This method is used internally by
   * {@link Event#publish(hr.yeti.rudimentary.events.EventPublisher.Type)} for publishing events.
   * For now only synchronous publishing is supported which means listeners will
   * be executed synchronously one after another.
   * </pre>
   *
   * @param event Event instance to be published.
   * @param type How event will be published. Currently only Type.SYNC is supported.
   */
  public void publish(Event event, Type type) {
    if (LISTENERS.containsKey(event.getClass())) {
      LISTENERS.get(event.getClass())
          .stream()
          .forEach((t) -> { // synchronous only for now.
            t.onEvent(event);
          });
    }
  }

  @Override
  public void initialize() {
    Instance.providersOf(EventListener.class)
        .forEach(this::attach);
  }

  /**
   * Adds event listener to LISTENERS map.
   *
   * @param listener Registered event listener to be added to LISTENERS map.
   */
  private void attach(EventListener<Event> listener) {
    String className = ((ParameterizedType) listener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName();
    try {
      Class<? extends Event> clazz = (Class<? extends Event>) Class.forName(className);
      LISTENERS.putIfAbsent(clazz, new ArrayList<>());
      if (!LISTENERS.get(clazz).contains(listener)) {
        LISTENERS.get(clazz).add(listener);
      }
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(EventPublisher.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public Class[] dependsOn() {
    return new Class[]{ EventListener.class };
  }

}
