package hr.yeti.rudimentary.events;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.events.spi.EventListener;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vedransmid@yeti-it.hr
 */
public final class EventPublisher implements Instance {

  public enum Type {
    SYNC, ASYNC
  }

  private static final Map<Class<?>, List<EventListener<Event>>> LISTENERS = new ConcurrentHashMap<>();

  public void publish(Event event, Type type) {
    if (LISTENERS.containsKey(event.getClass())) {
      LISTENERS.get(event.getClass())
          .stream()
          .forEach((t) -> {
            t.onEvent(event);
          });
    }
  }

  @Override
  public void initialize() {
    Instance.providersOf(EventListener.class)
        .forEach(this::attach);
  }

  private void attach(EventListener<Event> listener) {
    String className = ((ParameterizedType) listener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName();
    try {
      Class<?> clazz = Class.forName(className);
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
