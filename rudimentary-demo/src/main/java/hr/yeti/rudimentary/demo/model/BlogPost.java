package hr.yeti.rudimentary.demo.model;

import hr.yeti.rudimentary.events.Event;
import hr.yeti.rudimentary.http.content.Pojo;

public class BlogPost extends Pojo implements Event {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
