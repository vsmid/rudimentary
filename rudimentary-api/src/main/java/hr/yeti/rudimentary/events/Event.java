package hr.yeti.rudimentary.events;

import hr.yeti.rudimentary.context.spi.Instance;

/**
 * Extend this class if you want to give a class event publishing feature.
 *
 * <pre>
 * {@code
 * // imports...
 *
 * public class BlogPost extends Event {
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
public abstract class Event {

  /**
   * Publishes this object instance as an event.
   *
   * @param type Should event be published synchronously or asynchronously.
   */
  public void publish(EventPublisher.Type type) {
    Instance.of(EventPublisher.class).publish(this, type);
  }

}
