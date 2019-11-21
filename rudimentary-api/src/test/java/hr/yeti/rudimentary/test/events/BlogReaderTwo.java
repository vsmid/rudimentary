package hr.yeti.rudimentary.test.events;

import hr.yeti.rudimentary.events.spi.EventListener;

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
