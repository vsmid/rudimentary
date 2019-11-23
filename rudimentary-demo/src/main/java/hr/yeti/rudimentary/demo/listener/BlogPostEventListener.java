package hr.yeti.rudimentary.demo.listener;

import hr.yeti.rudimentary.demo.model.BlogPost;
import hr.yeti.rudimentary.events.spi.EventListener;

public class BlogPostEventListener implements EventListener<BlogPost> {

    @Override
    public void onEvent(BlogPost event) {
        String text = event.getText();
        event.setText(text + " value was changed by the listener.");
    }

}
