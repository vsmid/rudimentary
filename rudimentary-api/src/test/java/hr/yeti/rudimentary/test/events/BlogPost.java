package hr.yeti.rudimentary.test.events;

import hr.yeti.rudimentary.events.Event;

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
