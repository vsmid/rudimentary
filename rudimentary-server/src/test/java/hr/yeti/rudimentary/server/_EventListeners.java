package hr.yeti.rudimentary.server;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.events.spi.EventListener;
import java.util.concurrent.TimeUnit;

public class _EventListeners {

    public static class BlogPostListener implements EventListener<_Models.BlogPost> {

        @Override
        public void onEvent(_Models.BlogPost event) {
            try {
                // Simulate operation time
                TimeUnit.MILLISECONDS.sleep(25);
                event.text = event.text.toUpperCase();
                Instance.of(_HttpEndpoints.BlogPostEventPublishingEndpoint.class).text = event.text;
            } catch (InterruptedException ex) {
                logger().log(System.Logger.Level.ERROR, ex);
            }
        }

    }
}
