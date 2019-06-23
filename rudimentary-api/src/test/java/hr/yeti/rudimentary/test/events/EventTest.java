package hr.yeti.rudimentary.test.events;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.events.EventPublisher;
import hr.yeti.rudimentary.events.spi.EventListener;
import hr.yeti.rudimentary.test.ContextMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import hr.yeti.rudimentary.events.Event;

public class EventTest {

  @Test
  public void test_publish_event() {
    // setup:
    ContextMock context = new ContextMock(
        new BlogReaderOne(),
        new BlogReaderTwo(),
        new EventPublisher()
    );

    when: // Each class extending Event can publish an event.
    new BlogPost("Post 1.").publish(EventPublisher.Type.SYNC);

    then:
    assertEquals("BlogPost{text=Post 1.}", Instance.of(BlogReaderOne.class).lastMessage);
    assertEquals("BlogPost{text=Post 1.}", Instance.of(BlogReaderTwo.class).lastMessage);

    and:

    when: // Event can be published using EventPublisher via Instance.
    Instance.of(EventPublisher.class).publish(new BlogPost("Post 2."), EventPublisher.Type.SYNC);

    then:
    assertEquals("BlogPost{text=Post 2.}", Instance.of(BlogReaderOne.class).lastMessage);
    assertEquals("BlogPost{text=Post 2.}", Instance.of(BlogReaderTwo.class).lastMessage);
  }

  public class BlogReaderOne implements EventListener<BlogPost> {

    private String lastMessage;

    @Override
    public void onEvent(BlogPost event) {
      lastMessage = event.toString();
    }

    public String getLastMessage() {
      return lastMessage;
    }

  }

  public class BlogReaderTwo implements EventListener<BlogPost> {

    private String lastMessage;

    @Override
    public void onEvent(BlogPost event) {
      lastMessage = event.toString();
    }

    public String getLastMessage() {
      return lastMessage;
    }

  }

  public class BlogPost implements Event {

    private String text;

    public BlogPost(String text) {
      this.text = text;
    }

    public String getText() {
      return text;
    }

    @Override
    public String toString() {
      return "BlogPost{" + "text=" + text + '}';
    }

  }

}
