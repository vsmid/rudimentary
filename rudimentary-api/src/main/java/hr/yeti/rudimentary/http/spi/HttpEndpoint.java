package hr.yeti.rudimentary.http.spi;

import com.sun.net.httpserver.Headers;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.exception.ExceptionInfo;
import hr.yeti.rudimentary.exception.spi.ExceptionHandler;
import hr.yeti.rudimentary.http.*;
import hr.yeti.rudimentary.http.content.Form;
import hr.yeti.rudimentary.http.content.Html;
import hr.yeti.rudimentary.http.content.Json;
import hr.yeti.rudimentary.http.content.Model;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.interceptor.spi.AfterInterceptor;
import hr.yeti.rudimentary.validation.Constraints;

import java.net.URI;
import java.util.Map;
import java.util.ServiceLoader;

// TODO Write about how to include single http endpoint via jar and module-info.jar.
/**
 * This is a central SPI for exposing functionalities over HTTP.
 *
 * Since this interface extends {@link Instance} it means it is loaded automatically via {@link ServiceLoader} on application startup.
 *
 * You can have as many different HttpEndpoint implementations as you want and you can register them in <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.http.spi.HttpEndpoint</i>
 * file.
 *
 * @author vedransmid@yeti-it.hr
 * @param <I> Type of request body.
 * @param <O> Type of outgoing response.
 */
public interface HttpEndpoint<I extends Model, O extends Model> extends Instance {

    /**
     * Sets HTTP method. Defaults to GET.
     *
     * @see HttpMethod
     * @return HTTP method used by implementing class.
     */
    default HttpMethod httpMethod() {
        return HttpMethod.GET;
    }

    /**
     * Sets URI. Defaults to implementing class name with starting letter lower cased.
     *
     * @return URI on which implementing class will be available.
     */
    default URI path() {
        String path = this.getClass().getSimpleName();
        return URI.create(
                "/" + path.substring(0, 1).toLowerCase() + path.substring(1)
        );
    }

    /**
     * Sets response HTTP status. Defaults to 200 - OK.
     *
     * @return HTTP status returned in a response.
     */
    default int httpStatus() {
        return 200;
    }

    /**
     * Local before interceptor. This interceptor will apply only to implementing class. Implement this method to execute any kind of logic before
     * {@link HttpEndpoint#response(hr.yeti.rudimentary.http.Request)} method is executed. Also, this method will be executed after global {@link AfterInterceptor}.
     *
     * @param request Incoming HTTP request abstraction.
     */
    default void before(Request<I> request) {

    }

    /**
     * This is the single most important method of this interface. This is also the only method which requires implementation upon declaration. This is basically a place where you implement business
     * logic exposed by implementing class.
     *
     * @param request Incoming HTTP request abstraction.
     * @return Response type depending on the inferred generic type.
     */
    O response(Request<I> request);

    // TODO Maybe use custom model for http headers ?
    /**
     * Sets HTTP headers to be return in a response.
     *
     * @param requestHeaders Incoming http request headers.
     * @return HTTP headers.
     */
    default Headers responseHttpHeaders(Headers requestHeaders) {
        return new Headers();
    }

    /**
     * Local after interceptor. This interceptor will apply only to implementing class. Implement this method to execute any kind of logic after
     * {@link HttpEndpoint#response(hr.yeti.rudimentary.http.Request)} method is executed and no exception is thrown. Also, this method will be executed after global {@link AfterInterceptor}.
     *
     * @param request Incoming HTTP request abstraction.
     * @param response Outgoing response.
     */
    default void after(Request<I> request, O response) {

    }

    /**
     * Local exception handler. This interceptor will apply only to implementing class. If this method is implemented it will have greater priority that the global {@link ExceptionHandler} when
     * handling exceptions.
     *
     * @param e Exception thrown during {@link HttpEndpoint#response(hr.yeti.rudimentary.http.Request)} method execution.
     * @return Exception information.
     */
    default ExceptionInfo onException(Exception e) {
        return ExceptionInfo.defaultExceptionInfo();
    }

    /**
     * Convenience method to have a logger available out-of-the box. By default, this logger will use standard Java logger unless a different provider is configured. In both cases, use this logger to
     * log. In most cases, there should be no need to override this method whether you choose to log with custom logging provider or with default Java logging provider.
     *
     * @return System logger.
     */
    default System.Logger logger() {
        return System.getLogger(this.getClass().getName());
    }

    /**
     * <pre>
     * Sets validation constraints for data received via incoming HTTP request. This goes for request
     * body, request parameters, URI path variables and HTTP headers.
     *
     * For request content types of {@link Form}, {@link Json}, {@link Html} and {@link Text} you can
     * freely define constraints on body without any considerations. Moreover, for those this is the
     * only place where you can define constraints. However, in case of custom {@link Model} body
     * content type you have a couple of options:
     *
     * <ul>
     * <li>Define constraints for body on the custom content type level using
     * {@link Model#constraints()} method.</li>
     * <li>Define constraints for body on the HttpEndpoint level using
     * {@link HttpEndpoint#constraints(hr.yeti.rudimentary.http.content.Model, java.util.Map, java.util.Map,
     * com.sun.net.httpserver.Headers)} method.</li>
     * <li>Define constraints for body on both of the above levels.</li>
     * </ul>
     *
     * Important thing to remember is that constraints defined on any level will be executed. Nothing
     * gets excluded. So keep that in mind when you want to define constraints for custom
     * {@link Model} body on both levels.
     *
     * General advice for custom {@link Model} body constraints would be to keep all the constraint
     * definitions inside the custom model itself.
     * </pre>
     *
     * @param body Request body.
     * @param pathVariables Path variables.
     * @param queryParameters Query parameters.
     * @param httpHeaders HTTP headers.
     * @return Set of validation constraints for the given parameters..
     */
    default Constraints constraints(
            I body,
            Map<String, String> pathVariables,
            Map<String, String> queryParameters,
            Headers httpHeaders
    ) {
        return new Constraints();
    }

    /**
     * Sets information about what this endpoint is suppose to be doing. This will be printed out on startup and will also be used in auto generated docs.
     *
     * @return Endpoint description.
     */
    default String description() {
        return "";
    }
}
