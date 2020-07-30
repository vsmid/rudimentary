package hr.yeti.rudimentary.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.content.Model;
import hr.yeti.rudimentary.http.session.Session;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.security.Identity;
import hr.yeti.rudimentary.http.value.Transformable;
import hr.yeti.rudimentary.http.value.TransformableValue;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private Headers requestHttpHeaders;
    private T body;
    private Map<String, Object> pathVariables;
    private Map<String, Object> queryParameters;
    private URI uri;
    private HttpExchange httpExchange;

    private int responseHttpStatus;

    public Request(
        Identity identity,
        Headers httpHeaders,
        T body,
        Map<String, Object> pathVariables,
        Map<String, Object> queryParameters,
        URI uri,
        HttpExchange httpExchange) {
        this.identity = identity;
        this.requestHttpHeaders = httpHeaders;
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
        return requestHttpHeaders.get("Cookie")
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

    public Headers getRequestHttpHeaders() {
        return requestHttpHeaders;
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

    public Transformable pathVar(String name) {
        return new TransformableValue(getPathVariables().get(name).toString());
    }

    public Transformable queryParam(String name) {
        return new TransformableValue(getQueryParameters().get(name).toString());
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

    public int getResponseHttpStatus() {
        return responseHttpStatus;
    }

    /**
     * Overrides {@link HttpEndpoint#httpStatus() }.
     *
     * @param responseHttpStatus Http response status to be returned.
     */
    public void setResponseHttpStatus(int responseHttpStatus) {
        this.responseHttpStatus = responseHttpStatus;
    }

    public Headers getResponseHttpHeaders() {
        return this.httpExchange.getResponseHeaders();
    }

    /**
     * Overrides {@link HttpEndpoint#responseHttpHeaders(hr.yeti.rudimentary.http.Request, hr.yeti.rudimentary.http.content.Model)
     * }.
     *
     * @param name Http header name.
     * @param value Http header value.
     */
    public void addResponseHttpHeader(String name, String value) {
        getResponseHttpHeaders().add(name, value);
    }

}
