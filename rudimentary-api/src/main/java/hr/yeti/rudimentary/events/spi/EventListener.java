package hr.yeti.rudimentary.events.spi;

import hr.yeti.rudimentary.context.spi.Instance;
import java.util.ServiceLoader;
import hr.yeti.rudimentary.events.Event;

/**
 * Since this interface inherently extends {@link Instance} it means it is loaded automatically via
 * {@link ServiceLoader} on application startup.
 *
 * You can have as many different EventListener implementations as you want and you can register
 * them in
 * <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.events.spi.EventListener</i>
 * file.
 *
 * Implement this class if you would like to execute some business logic when certain event occurs.
 *
 * @author vedransmid@yeti-it.hr
 * @param <T> Type of event message.
 */
public interface EventListener<T extends Event> extends Instance {

  /**
   * Method executed when event of type T occurs.
   *
   * @param event Event containing some data.
   */
  public void onEvent(T event);

}
