package hr.yeti.rudimentary.test.events;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.events.EventPublisher;
import hr.yeti.rudimentary.test.ContextMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.util.Map;

public class EventTest {

    @Test
    public void test_publish_event() {
        // setup:
        ContextMock context = new ContextMock(
                Map.of(),
                BlogReaderOne.class,
                BlogReaderTwo.class,
                EventPublisher.class
        );

        when: // Each class extending Event can publish an event.
        new BlogPost("Post 1.").publish(EventPublisher.Type.SYNC);

        then:
        assertEquals("BlogPost{text=Post 1.}", Instance.of(BlogReaderOne.class).getLastMessage());
        assertEquals("BlogPost{text=Post 1.}", Instance.of(BlogReaderTwo.class).getLastMessage());

        and:

        when: // Event can be published using EventPublisher via Instance.
        Instance.of(EventPublisher.class).publish(new BlogPost("Post 2."), EventPublisher.Type.SYNC);

        then:
        assertEquals("BlogPost{text=Post 2.}", Instance.of(BlogReaderOne.class).getLastMessage());
        assertEquals("BlogPost{text=Post 2.}", Instance.of(BlogReaderTwo.class).getLastMessage());
    }

}
