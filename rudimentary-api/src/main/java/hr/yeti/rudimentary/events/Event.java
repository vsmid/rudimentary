package hr.yeti.rudimentary.events;

import hr.yeti.rudimentary.context.spi.Instance;

/**
 * Implement this class if you want to give a class event publishing feature.
 *
 * <pre>
 * {@code
 * // imports...
 *
 * public class BlogPost implements Event {
 *
 *  private String text;
 *
 *  public BlogPost(String text) {
 *    this.text = text;
 *  }
 *
 *  public String getText() {
 *    return this.text;
 *  }
 *
 * }
 *
 * ...
 * // Publish event synchronously
 * new BlogPost("This my first blog post event.").publish(EventPublisher.Type.SYNC);
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public interface Event {

  /**
   * Publishes this object instance as an event. Default implementation should generally not be
   * overridden.
   *
   * @param type Should event be published synchronously or asynchronously.
   */
  default void publish(EventPublisher.Type type) {
    Instance.of(EventPublisher.class).publish(this, type);
  }

}
