package hr.yeti.rudimentary.events;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.events.spi.EventListener;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A little bit custom implementation of observer pattern.
 *
 * Since this class implements {@link Instance} it means it is loaded automatically via {@link ServiceLoader} on
 * application startup.
 *
 * In practice, the only time you could use an instance of this class if you would want to publish an event. Preferred
 * way of publishing events is shown in {@link Event}. By default, an instance of EventPublisher should be provided by the implementation which in our
 * case is rudimentary-server module.
 *
 * <pre>
 * {@code
 *  ...
 *  Instance.of(EventPublisher.class).publish(new BlogPost("Post 2."), EventPublisher.Type.SYNC);
 *  ...
 * }
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public final class EventPublisher implements Instance {

    /**
     * Enumerated type of how event should be published. This has an effect on the way how listeners process published
     * event.
     */
    public enum Type {
        SYNC, ASYNC
    }

    /**
     * A map of all registered {@link EventListener}. One event class type can have multiple listeners registered.
     */
    private static final Map<Class<? extends Event>, List<EventListener<Event>>> LISTENERS = new ConcurrentHashMap<>();

    /**
     * Thread pool for asynchronous listener execution.
     */
    private static final ExecutorService ASYNC_EXECUTOR = Executors.newCachedThreadPool();

    /**
     * <pre>
     * Call this class to publish an event. This method is used internally by
     * {@link Event#publish(hr.yeti.rudimentary.events.EventPublisher.Type)} for publishing events.
     * For now only synchronous publishing is supported which means listeners will
     * be executed synchronously one after another.
     * </pre>
     *
     * @param event Event instance to be published.
     * @param type How event will be published.
     */
    public void publish(Event event, Type type) {
        if (LISTENERS.containsKey(event.getClass())) {
            if (type == Type.SYNC) {
                LISTENERS.get(event.getClass())
                    .stream()
                    .forEach((t) -> {
                        t.onEvent(event);
                    });
            } else {
                LISTENERS.get(event.getClass())
                    .stream()
                    .map(listener -> {
                        return (Runnable) () -> {
                            listener.onEvent(event);
                        };
                    })
                    .forEach((runnable) -> {
                        CompletableFuture.runAsync(runnable, ASYNC_EXECUTOR);
                    });
            }
        }
    }

    @Override
    public void initialize() {
        Instance.providersOf(EventListener.class)
            .forEach(this::attach);
    }

    /**
     * Adds event listener to LISTENERS map.
     *
     * @param listener Registered event listener to be added to LISTENERS map.
     */
    private void attach(EventListener<Event> listener) {
        String className = ((ParameterizedType) listener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName();
        try {
            Class<? extends Event> clazz = (Class<? extends Event>) Class.forName(className);
            LISTENERS.putIfAbsent(clazz, new ArrayList<>());
            if (!LISTENERS.get(clazz).contains(listener)) {
                LISTENERS.get(clazz).add(listener);
            }
        } catch (ClassNotFoundException ex) {
           logger().log(System.Logger.Level.ERROR, ex);
        }
    }

    @Override
    public Class[] dependsOn() {
        return new Class[]{ EventListener.class };
    }

}
