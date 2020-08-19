package hr.yeti.rudimentary.http.content.handler.spi;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.content.Model;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.mvc.spi.ViewEndpoint;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ServiceLoader;

/**
 * <pre>
 * SPI to implement if you would like to create new content handler.
 *
 * Content handlers are in charge of converting incoming request body and outgoing response body based on
 * types defined in {@link HttpEndpoint}.
 *
 * Since this class implements {@link Instance} it means it is loaded automatically via {@link ServiceLoader} on
 * application startup.
 *
 * You can have as many different ContentHandler implementations as you want and you can register them in
 * <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.http.content.handler.spi.ContentHandler</i>
 * file.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 * @param <T>
 */
public interface ContentHandler<T extends Model> extends Instance {

    /**
     *
     * @param httpExchange
     * @param httpEndpoint
     * @return
     * @throws java.io.IOException
     */
    T read(HttpExchange httpExchange, Class<? extends HttpEndpoint> httpEndpoint) throws IOException;

    /**
     *
     * @param httpStatus
     * @param data
     * @param httpExchange
     * @param httpEndpoint
     * @throws java.io.IOException
     */
    void write(int httpStatus, T data, HttpExchange httpExchange, Class<? extends HttpEndpoint> httpEndpoint) throws IOException;

    /**
     *
     * @param httpEndpoint
     * @param httpExchange
     * @return
     */
    default boolean activateReader(Class<? extends HttpEndpoint> httpEndpoint, HttpExchange httpExchange) {
        try {
            return getGenericType(httpEndpoint, 0).isAssignableFrom(getGenericType(this.getClass(), 0));
        } catch (ClassNotFoundException ex) {
            logger().log(System.Logger.Level.ERROR, ex);
            return false;
        }
    }

    /**
     *
     * @param httpEndpoint
     * @param model
     * @param httpExchange
     * @return
     */
    default boolean activateWriter(Class<? extends HttpEndpoint> httpEndpoint, HttpExchange httpExchange) {
        try {
            Class<? extends Model> clazz = isViewEndpoint(httpEndpoint) ? View.class : getGenericType(httpEndpoint, 1);
            return clazz.isAssignableFrom(getGenericType(this.getClass(), 0));
        } catch (ClassNotFoundException ex) {
            logger().log(System.Logger.Level.ERROR, ex);
            return false;
        }
    }

    public static Class<? extends Model> getGenericType(Class<?> clazz, int index) throws ClassNotFoundException {
        try {
            String className = ((ParameterizedType) clazz.getGenericInterfaces()[0]).getActualTypeArguments()[index].getTypeName();
            return (Class<? extends Model>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class is not parametrized with generic type.", e);
        }
    }

    public static boolean isViewEndpoint(Class<?> clazz) {
        return clazz.getGenericInterfaces()[0].getTypeName().startsWith(ViewEndpoint.class.getCanonicalName());
    }
}
