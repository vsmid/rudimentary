package hr.yeti.rudimentary.server.http.processor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.exception.ExceptionInfo;
import hr.yeti.rudimentary.exception.spi.ExceptionHandler;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.HttpRequestUtils;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.URIUtils;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Form;
import hr.yeti.rudimentary.http.content.Html;
import hr.yeti.rudimentary.http.content.Json;
import hr.yeti.rudimentary.http.content.Model;
import hr.yeti.rudimentary.http.content.StaticResource;
import hr.yeti.rudimentary.http.content.ByteStream;
import hr.yeti.rudimentary.http.content.Redirect;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.interceptor.spi.AfterInterceptor;
import hr.yeti.rudimentary.interceptor.spi.BeforeInterceptor;
import hr.yeti.rudimentary.mvc.spi.ViewEngine;
import hr.yeti.rudimentary.security.Identity;
import hr.yeti.rudimentary.server.http.HttpEndpointContextProvider;
import hr.yeti.rudimentary.validation.ConstraintViolations;
import hr.yeti.rudimentary.validation.Constraints;
import hr.yeti.rudimentary.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.json.JsonValue;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import javax.json.stream.JsonParsingException;

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
                        respond(405, ("Http method " + httpMethod + " is not supported.").getBytes(), exchange);
                        return;
                    }

                    HttpEndpoint httpEndpoint = httpEndpointMatchInfo.getHttpEndpoint();

                    // Request body
                    Object value = null;
                    Class requestBodyModelType;
                    try {
                        requestBodyModelType = HttpRequestUtils.getRequestBodyType(httpEndpoint.getClass());
                    } catch (ClassNotFoundException e) {
                        respond(500, "Internal server error.".getBytes(), exchange);
                        return;
                    }

                    // Path & query parsing
                    Map<String, String> pathVariables = HttpRequestUtils.parsePathVariables(
                        URIUtils.prependSlashPrefix(httpEndpoint.path()), path
                    );
                    Map<String, Object> queryParameters = HttpRequestUtils.parseQueryParameters(path.getQuery());

                    List<Constraints> constraintsList = new ArrayList<>();

                    try {

                        if (requestBodyModelType.isAssignableFrom(Empty.class)) {
                            value = new Empty();
                        } else if (requestBodyModelType.isAssignableFrom(Json.class)) {
                            value = new Json(JsonbBuilder.create().fromJson(exchange.getRequestBody(), JsonValue.class));
                        } else if (requestBodyModelType.isAssignableFrom(Text.class)) {
                            value = new Text(new String(exchange.getRequestBody().readAllBytes()));
                        } else if (requestBodyModelType.isAssignableFrom(Html.class)) {
                            value = new Html(new String(exchange.getRequestBody().readAllBytes()));
                        } else if (requestBodyModelType.isAssignableFrom(Form.class)) {
                            String form = new String(exchange.getRequestBody().readAllBytes());
                            value = new Form(HttpRequestUtils.parseQueryParameters(form));
                        } else if (requestBodyModelType.isAssignableFrom(ByteStream.class)) {
                            value = new ByteStream(exchange.getRequestBody());
                        } else {
                            // POJO assumed
                            value = JsonbBuilder.create().fromJson(exchange.getRequestBody(), requestBodyModelType);
                            constraintsList.add(((Model) value).constraints());
                        }
                    } catch (JsonbException | JsonParsingException | NoSuchElementException ex) {
                        respond(400, "Bad request.".getBytes(), exchange);
                        return;
                    }

                    // Construct request object
                    Request request = new Request(
                        (Identity) exchange.getPrincipal(),
                        exchange.getRequestHeaders(),
                        (Model) value,
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
                            .forEach((reason) -> {
                                exchange.getResponseHeaders().add("Reason", reason);
                            });
                        respond(400, "Bad request".getBytes(), exchange);
                        return;
                    }

                    // Check authorizations
                    Predicate<Request> authorizations = httpEndpoint.authorizations();
                    Optional<Predicate<Request>> authorizationChain = Stream.of(authorizations).reduce(Predicate::and);
                    if (authorizationChain.isPresent()) {
                        boolean authorized = authorizationChain.get().test(request);
                        if (!authorized) {
                            respond(403, ("Not authorized.").getBytes(), exchange);
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
                        ExceptionInfo exceptionInfo = httpEndpoint.onException(e);

                        if (!exceptionInfo.isOverride()) {
                            // Activate global exception handler if provided and http endpoint does not override
                            // default onException.
                            if (Objects.nonNull(globalExceptionHandler)) {
                                exceptionInfo = globalExceptionHandler.onException(e);
                            } else if (Objects.isNull(globalExceptionHandler)) {
                                // Print stack trace if no custom exception handler is provided.
                                e.printStackTrace();
                            }
                        }

                        respond(exceptionInfo.getHttpStatus(), exceptionInfo.getDescription().getBytes(StandardCharsets.UTF_8), exchange);
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

                    byte[] responseTransformed = null;

                    // Set http endpoint defined http headers
                    //exchange.getResponseHeaders().putAll(httpEndpoint.responseHttpHeaders(request, (Model) response));
                    if (response instanceof Empty) {
                        exchange.getResponseHeaders().put("Content-Type", List.of(MediaType.ALL));
                        responseTransformed = "".getBytes(StandardCharsets.UTF_8);
                    } else if (response instanceof Json) {
                        exchange.getResponseHeaders().put("Content-Type", List.of(MediaType.APPLICATION_JSON));
                        responseTransformed = ((Json) response).get().toString().getBytes(StandardCharsets.UTF_8);
                    } else if (response instanceof Text) {
                        exchange.getResponseHeaders().put("Content-Type", List.of(MediaType.TEXT_PLAIN));
                        responseTransformed = ((Text) response).get().getBytes(StandardCharsets.UTF_8);
                    } else if (response instanceof Html) {
                        exchange.getResponseHeaders().put("Content-Type", List.of(MediaType.TEXT_HTML));
                        responseTransformed = ((Html) response).get().getBytes(StandardCharsets.UTF_8);
                    } else if (response instanceof View) {
                        exchange.getResponseHeaders().put("Content-Type", List.of(MediaType.TEXT_HTML));

                        View view = (View) response;

                        if (Objects.nonNull(Instance.of(ViewEngine.class))) {
                            responseTransformed = view.get().getBytes(StandardCharsets.UTF_8);
                        } else {
                            respond(500, "Could not resolve view.".getBytes(StandardCharsets.UTF_8), exchange);
                            return;
                        }

                    } else if (response instanceof StaticResource) {
                        StaticResource staticResource = (StaticResource) response;

                        exchange.getResponseHeaders().put("Content-Type", List.of(staticResource.getMediaType()));

                        try ( InputStream is = staticResource.get()) {
                            responseTransformed = is.readAllBytes();
                        }

                    } else if (response instanceof ByteStream) {
                        ByteStream streamOut = (ByteStream) response;
                        respondWithStream(request.getResponseHttpStatus() != 0 ? request.getResponseHttpStatus() : httpEndpoint.httpStatus(), streamOut, exchange);
                        return;
                    } else if (response instanceof Redirect) {
                        Redirect redirect = (Redirect) response;
                        if (Objects.nonNull(redirect)) {
                            exchange.getResponseHeaders().add("location", redirect.get().toString());
                            respond(request.getResponseHttpStatus() != 0 ? request.getResponseHttpStatus() : redirect.getHttpStatus(), null, exchange);
                            return;
                        }
                    } else {
                        // POJO assumed
                        exchange.getResponseHeaders().put("Content-Type", List.of(MediaType.APPLICATION_JSON));
                        responseTransformed = JsonbBuilder.create().toJson((response)).getBytes(StandardCharsets.UTF_8);
                    }

                    respond(request.getResponseHttpStatus() != 0 ? request.getResponseHttpStatus() : httpEndpoint.httpStatus(), responseTransformed, exchange);

                } else {
                    respond(404, null, exchange);
                }
            } catch (NullPointerException e) {
                respond(500, null, exchange);
            }
        } catch (IOException e) {
            System.getLogger(this.getClass().getName()).log(System.Logger.Level.ERROR, e);
        }

    }

    private void respond(int httpStatus, byte[] message, HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(httpStatus, Objects.isNull(message) ? 0 : message.length);
        if (Objects.nonNull(message)) {
            httpExchange.getResponseBody().write(message);
            httpExchange.getResponseBody().flush();
        }
        httpExchange.close();
    }

    private void respondWithStream(int httpStatus, ByteStream stream, HttpExchange httpExchange) throws IOException {
        if (Objects.nonNull(stream)) {
            httpExchange.sendResponseHeaders(httpStatus, 0);
            stream.getStreamOutWriteDef().startStreaming(httpExchange.getResponseBody());
            try {
                httpExchange.getResponseBody().flush();
                httpExchange.getResponseBody().close();
            } catch (IOException ex) {
                //noop
            }
        }
        httpExchange.close();
    }

    @Override
    public Class[] dependsOn() {
        return new Class[]{ Config.class };
    }

}
