package hr.yeti.rudimentary.http.content.handler.spi;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.HttpEndpointUtils;
import hr.yeti.rudimentary.http.content.Model;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.IOException;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    T read(HttpExchange httpExchange, Class<HttpEndpoint> httpEndpoint) throws IOException;

    /**
     *
     * @param httpStatus
     * @param data
     * @param httpExchange
     * @param httpEndpoint
     * @throws java.io.IOException
     */
    void write(int httpStatus, T data, HttpExchange httpExchange, Class<HttpEndpoint> httpEndpoint) throws IOException;

    /**
     *
     * @param httpEndpoint
     * @param model
     * @param httpExchange
     * @return
     */
    default boolean activateReader(Class<HttpEndpoint> httpEndpoint, Class<T> model, HttpExchange httpExchange) {
        try {
            return HttpEndpointUtils.getRequestBodyType(httpEndpoint).isAssignableFrom(model);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ContentHandler.class.getName()).log(Level.SEVERE, null, ex);
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
    default boolean activateWriter(Class<HttpEndpoint> httpEndpoint, Class<T> model, HttpExchange httpExchange) {
        try {
            
            return HttpEndpointUtils.getResponseBodyType(httpEndpoint).isAssignableFrom(model);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ContentHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
