package hr.yeti.rudimentary.events;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.events.spi.EventListener;
import hr.yeti.rudimentary.events.spi.EventPublisher;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Event implements EventPublisher {

  private static final Map<Class<?>, List<EventListener<EventMessage>>> LISTENERS = new ConcurrentHashMap<>();

  public static void publish(EventMessage eventMessage) {
    if (LISTENERS.containsKey(eventMessage.getClass())) {
      LISTENERS.get(eventMessage.getClass())
          .stream()
          .forEach((t) -> {
            t.onMessage(eventMessage);
          });
    }
  }

  @Override
  public void initialize() {
    Instance.providersOf(EventListener.class)
        .forEach(this::attach);
  }

  private void attach(EventListener<EventMessage> listener) {
    String className = ((ParameterizedType) listener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName();
    try {
      Class<?> clazz = Class.forName(className);
      LISTENERS.putIfAbsent(clazz, new ArrayList<>());
      if (!LISTENERS.get(clazz).contains(listener)) {
        LISTENERS.get(clazz).add(listener);
      }
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(Event.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void detach(EventListener<EventMessage> listener) {
    String className = ((ParameterizedType) listener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName();
    try {
      Class<?> clazz = Class.forName(className);
      List<EventListener<EventMessage>> listeners = LISTENERS.get(clazz);
      if (Objects.nonNull(listeners)) {
        listeners.remove(listener);
      }
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(Event.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public Class[] dependsOn() {
    return new Class[]{ EventListener.class };
  }

}
