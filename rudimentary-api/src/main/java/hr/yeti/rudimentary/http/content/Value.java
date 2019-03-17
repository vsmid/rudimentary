package hr.yeti.rudimentary.http.content;

/**
 * <pre>
 * All content types that can be read or written should implement this interface.
 * This to have a standardized way of value access through {@link Value#getValue()} method.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 * @param <T> Value type.
 */
public interface Value<T> {

  T getValue();
}
