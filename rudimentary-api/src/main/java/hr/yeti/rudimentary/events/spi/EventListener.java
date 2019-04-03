package hr.yeti.rudimentary.events.spi;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.events.EventMessage;

public interface EventListener<T extends EventMessage> extends Instance {

  public void onMessage(T message);

}
