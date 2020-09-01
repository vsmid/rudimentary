## Events

### Introduction
Sometimes you want to decouple business logic by reacting to events.
Rudimentary offers simple way to dispatch events in synchronous and asynchronous manner.

### Creating event
To create an event you need to create a class which implements `hr.yeti.rudimentary.events.Event` interface.
```java
public class BlogPost implements Event {

  private String text;

  public String getText() {
      return text;
  }

  public void setText(String text) {
      this.text = text;
  }
}
```

### Publishing event
By implementing above mentioned interface BlogPost instances automatically gain access to  convenient `publish` method.
```java
new BlogPost("This is my first blog post event.").publish(EventPublisher.Type.SYNC); // Synchronous event
new BlogPost("This is my first blog post event.").publish(EventPublisher.Type.ASYNC); // Asynchronous event
```

### Reacting to event
To react to certain event you need to create class which implements `hr.yeti.rudimentary.events.spi.EventListener` interface. 
```java
public class BlogPostEventListener implements EventListener<BlogPost> {

  @Override
  public void onEvent(BlogPost event) {
      String text = event.getText();
      event.setText(text + " value was changed by the listener.");
  }

}
```
You can have as many different EventListener implementations as you want and you can register them in *src/main/resources/META-INF/services/hr.yeti.rudimentary.events.spi.EventListener*. This however, rudimentary-maven-plugin automatically does for you.
