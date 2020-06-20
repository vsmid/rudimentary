package hr.yeti.rudimentary.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.content.Model;
import hr.yeti.rudimentary.http.session.Session;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.security.Identity;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <pre>
 * Abstraction of incoming HTTP request.
 * Offers simple way to access data received via HTTP request.
 * Heavily used in {@link HttpEndpoint} methods.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 * @param <T> Incoming HTTP request body type.
 */
public class Request<T extends Model> {

    private Identity identity;
    private Headers httpHeaders;
    private T body;
    private Map<String, Object> pathVariables;
    private Map<String, Object> queryParameters;
    private URI uri;
    private HttpExchange httpExchange;

    public Request(
        Identity identity,
        Headers httpHeaders,
        T body,
        Map<String, Object> pathVariables,
        Map<String, Object> queryParameters,
        URI uri,
        HttpExchange httpExchange) {
        this.identity = identity;
        this.httpHeaders = httpHeaders;
        this.body = body;
        this.pathVariables = pathVariables;
        this.queryParameters = queryParameters;
        this.uri = uri;
        this.httpExchange = httpExchange;
    }

    /**
     * Gets HTTP cookies received in HTTP request.
     *
     * @return List of HTTP cookies.
     */
    public List<HttpCookie> getCookies() {
        return httpHeaders.get("Cookie")
            .stream()
            .map(c -> c.split("; "))
            .flatMap(Stream::of)
            .map(HttpCookie::parse)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    /**
     * Gets user details with custom details.
     *
     * @param <D> Type of user details.
     * @param details User details class.
     * @return User identity with custom details.
     */
    public <D> Identity<D> getIdentity(Class<D> details) {
        return getIdentity();
    }

    public Identity getIdentity() {
        return identity;
    }

    public Headers getHttpHeaders() {
        return httpHeaders;
    }

    public T getBody() {
        return body;
    }

    public Map<String, Object> getPathVariables() {
        return pathVariables;
    }

    public Map<String, Object> getQueryParameters() {
        return queryParameters;
    }

    public Object pathVar(String name) {
        return getPathVariables().get(name);
    }

    public Object queryParam(String name) {
        return getQueryParameters().get(name);
    }

    public URI getUri() {
        return uri;
    }

    public Session getSession() {
        return Session.acquire(httpExchange, true);
    }

    public HttpExchange getHttpExchange() {
        return httpExchange;
    }

}
