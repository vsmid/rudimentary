package hr.yeti.rudimentary.mvc.spi;

import hr.yeti.rudimentary.http.content.Model;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;

/**
 * Specialized type of {@link HttpEndpoint} used in MVC. It has a default response type set to
 * {@link View}.
 *
 * Since this interface inherently extends {@link Instance} it means it is loaded automatically via
 * {@link ServiceLoader} on application startup.
 *
 * You can have as many different ViewEndpoint providers as you want and you can register them in
 * <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.http.spi.HttpEndpoint</i>
 * file.
 *
 * For this endpoint to work properly it is required to have {@link ViewEngine} registered.
 *
 * @author vedransmid@yeti-it.hr
 * @param <I> Incoming HTTP request body type.
 */
public interface ViewEndpoint<I extends Model> extends HttpEndpoint<I, View> {

}
