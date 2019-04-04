package hr.yeti.rudimentary.events;

import hr.yeti.rudimentary.context.spi.Instance;

/**
 * @author vedransmid@yeti-it.hr
 */
public abstract class Event {

  public void publish(EventPublisher.Type type) {
    Instance.of(EventPublisher.class).publish(this, type);
  }

}
