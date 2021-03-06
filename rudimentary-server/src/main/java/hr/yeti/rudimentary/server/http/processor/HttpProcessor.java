package hr.yeti.rudimentary.server.http.processor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.exception.ExceptionInfo;
import hr.yeti.rudimentary.exception.spi.ExceptionHandler;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.HttpRequestUtils;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.URIUtils;
import hr.yeti.rudimentary.http.content.Model;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.interceptor.spi.AfterInterceptor;
import hr.yeti.rudimentary.interceptor.spi.BeforeInterceptor;
import hr.yeti.rudimentary.security.Identity;
import hr.yeti.rudimentary.server.http.HttpEndpointContextProvider;
import hr.yeti.rudimentary.validation.ConstraintViolations;
import hr.yeti.rudimentary.validation.Constraints;
import hr.yeti.rudimentary.validation.Validator;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

// TODO Cache content handler mapping to http endpoint
public class HttpProcessor implements HttpHandler, Instance {

    private ExceptionHandler globalExceptionHandler;

    @Override
    public void initialize() {
        this.globalExceptionHandler = Instance.of(ExceptionHandler.class);
    }

    @Override
    public void handle(HttpExchange exchange) {

        try {

            URI path = exchange.getRequestURI();
            HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());

            HttpEndpointContextProvider.HttpEndpointMatchInfo httpEndpointMatchInfo = Instance.of(HttpEndpointContextProvider.class).matchEndpoint(path, httpMethod);

            try {
                if (httpEndpointMatchInfo.isPathMatchFound()) {

                    if (Objects.isNull(httpEndpointMatchInfo.getHttpEndpoint())) {
                        respond(405, exchange);
                        return;
                    }

                    HttpEndpoint httpEndpoint = httpEndpointMatchInfo.getHttpEndpoint();

                    // Request body
                    Object body = null;

                    // Path & query parsing
                    Map<String, String> pathVariables = HttpRequestUtils.parsePathVariables(URIUtils.prependSlash(httpEndpoint.path()), path);
                    Map<String, Object> queryParameters = HttpRequestUtils.parseQueryParameters(path.getQuery());

                    List<Constraints> constraintsList = new ArrayList<>();

                    // Read request body
                    try {
                        Optional<ContentHandler> o = Instance.providersOf(ContentHandler.class)
                            .stream()
                            .filter(ch -> ch.activateReader(httpEndpoint.getClass(), exchange))
                            .findFirst();

                        if (o.isPresent()) {
                            body = o.get().read(exchange, httpEndpoint.getClass());
                            constraintsList.add(((Model) body).constraints());
                        } else {
                            logger().log(System.Logger.Level.ERROR, "No suitable request body content handler found.");
                            respond(500, exchange);
                            return;
                        }

                    } catch (IOException ex) {
                        respond(400, exchange);
                        return;
                    }

                    // Construct request object
                    Request request = new Request(
                        (Identity) exchange.getPrincipal(),
                        exchange.getRequestHeaders(),
                        (Model) body,
                        pathVariables,
                        queryParameters,
                        exchange.getRequestURI(),
                        exchange
                    );

                    constraintsList.add(httpEndpoint.constraints(request));

                    Constraints[] constraints = constraintsList.stream().toArray(Constraints[]::new);

                    ConstraintViolations violations = Validator.validate(constraints);
                    if (!violations.getList().isEmpty()) {

                        violations.getList()
                            .stream()
                            .filter(vr -> vr.getReason().isPresent())
                            .map(vr -> vr.getReason().get())
                            .forEach(reason -> exchange.getResponseHeaders().add("Reason", reason));

                        respond(400, exchange);
                        return;
                    }

                    // Check authorizations
                    Predicate<Request> authorizations = httpEndpoint.authorizations();
                    Optional<Predicate<Request>> authorizationChain = Stream.of(authorizations).reduce(Predicate::and);
                    if (authorizationChain.isPresent()) {
                        boolean authorized = authorizationChain.get().test(request);
                        if (!authorized) {
                            respond(403, exchange);
                            return;
                        }
                    }

                    // Before interceptor
                    List<BeforeInterceptor> beforeInterceptors = Instance.providersOf(BeforeInterceptor.class);
                    beforeInterceptors.sort(Comparator.comparing(BeforeInterceptor::order));

                    beforeInterceptors.forEach((interceptor) -> {
                        String normalizedApplyToURI = URIUtils.removeSlashPrefix(URI.create(interceptor.applyToURI())).toString();

                        //TODO Cache compiled patterns
                        if (Pattern.compile(normalizedApplyToURI).asPredicate().test(URIUtils.removeSlashPrefix(path).toString())) {
                            interceptor.intercept(request);
                        }
                    });

                    // Local http endpoint before interceptor
                    httpEndpoint.before(request);

                    // Creating response
                    Object response = null;
                    try {
                        // Set http endpoint defined http headers
                        exchange.getResponseHeaders().putAll(httpEndpoint.responseHttpHeaders(request, (Model) response));
                        response = httpEndpoint.response(request);
                    } catch (Exception e) {
                        ExceptionInfo exceptionInfo = httpEndpoint.onException(e, exchange.getResponseHeaders());

                        if (!exceptionInfo.isOverride()) {
                            // Activate global exception handler if provided and http endpoint does not override
                            // default onException.
                            if (Objects.nonNull(globalExceptionHandler)) {
                                exceptionInfo = globalExceptionHandler.onException(e, exchange.getResponseHeaders());
                            } else if (Objects.isNull(globalExceptionHandler)) {
                                // Print stack trace if no custom exception handler is provided.
                                e.printStackTrace();
                            }
                        }

                        respond(exceptionInfo.getHttpStatus(), exceptionInfo.getContent(), exchange);
                        return;
                    }

                    // Local http endpoint after interceptor
                    httpEndpoint.after(request, (Model) response);

                    // After interceptor
                    List<AfterInterceptor> afterInterceptors = Instance.providersOf(AfterInterceptor.class);
                    afterInterceptors.sort(Comparator.comparing(AfterInterceptor::order));

                    for (AfterInterceptor interceptor : afterInterceptors) {
                        String normalizedApplyToURI = URIUtils.removeSlashPrefix(URI.create(interceptor.applyToURI())).toString();

                        //TODO Cache compiled patterns
                        if (Pattern.compile(normalizedApplyToURI).asPredicate().test(URIUtils.removeSlashPrefix(path).toString())) {
                            interceptor.intercept(request, response);
                        }

                    }

                    // Write response
                    Optional<ContentHandler> o = Instance.providersOf(ContentHandler.class)
                        .stream()
                        .filter(ch -> ch.activateWriter(httpEndpoint.getClass(), exchange))
                        .findFirst();

                    if (o.isPresent()) {
                        o.get().write(request.getResponseHttpStatus() != 0
                            ? request.getResponseHttpStatus()
                            : httpEndpoint.httpStatus(),
                            (Model) response,
                            exchange,
                            httpEndpoint.getClass()
                        );
                        exchange.close();
                    } else {
                        logger().log(System.Logger.Level.ERROR, "No suitable response body content handler found.");
                        respond(500, exchange);
                    }

                } else {
                    respond(404, exchange);
                }
            } catch (NullPointerException e) {
                respond(500, exchange);
            }
        } catch (IOException e) {
            logger().log(System.Logger.Level.ERROR, e);
            try {
                respond(500, exchange);
            } catch (IOException ex) {
                logger().log(System.Logger.Level.ERROR, e);
            }
        }

    }

    private void respond(int httpStatus, HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            httpExchange.sendResponseHeaders(httpStatus, -1);
        }
    }

    private void respond(int httpStatus, byte[] message, HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            httpExchange.sendResponseHeaders(httpStatus, Objects.isNull(message) ? 0 : message.length);
            if (Objects.nonNull(message)) {
                httpExchange.getResponseBody().write(message);
                httpExchange.getResponseBody().flush();
            }
        }
    }

    @Override
    public Class[] dependsOn() {
        return new Class[]{ Config.class };
    }

}
