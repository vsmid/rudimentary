package hr.yeti.rudimentary.server.http.processor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.exception.ExceptionInfo;
import hr.yeti.rudimentary.exception.spi.ExceptionHandler;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Form;
import hr.yeti.rudimentary.http.content.Html;
import hr.yeti.rudimentary.http.content.Json;
import hr.yeti.rudimentary.http.content.Model;
import hr.yeti.rudimentary.http.content.StaticResource;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.interceptor.spi.AfterInterceptor;
import hr.yeti.rudimentary.interceptor.spi.BeforeInterceptor;
import hr.yeti.rudimentary.mvc.spi.ViewEngine;
import hr.yeti.rudimentary.security.Identity;
import hr.yeti.rudimentary.server.http.HttpEndpointContextProvider;
import hr.yeti.rudimentary.server.http.URIUtils;
import hr.yeti.rudimentary.validation.ConstraintViolations;
import hr.yeti.rudimentary.validation.Constraints;
import hr.yeti.rudimentary.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.json.JsonValue;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;

public class HttpProcessor implements HttpHandler {

  private final ExecutorService executorService;

  private ExceptionHandler globalExceptionHandler = Instance.of(ExceptionHandler.class);

  public HttpProcessor(int threadPoolSize) {
    this.executorService = Executors.newFixedThreadPool(threadPoolSize);
  }

  @Override
  public void handle(HttpExchange exchange) {

    executorService.execute(() -> {

      try {

        URI path = exchange.getRequestURI();
        HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());

        Optional<HttpEndpoint> httpEndpoint = Instance.of(HttpEndpointContextProvider.class).getHttpEndpoint(path, httpMethod);

        if (httpEndpoint.isPresent()) {

          if (httpMethod != httpEndpoint.get().httpMethod()) {
            respond(405, ("Http method " + httpMethod + " is not supported.").getBytes(), exchange);
            return;
          }

          // Request body
          Object value = null;
          Class requestBodyModelType;
          try {
            requestBodyModelType = httpEndpoint.get().requestType();
          } catch (ClassNotFoundException e) {
            respond(405, "Invalid request body.".getBytes(), exchange);
            return;
          }

          // Path & query parsing
          Map<String, String> pathVariables = URIUtils.parsePathVariables(httpEndpoint.get().path(), path);
          Map<String, Object> queryParameters = URIUtils.parseQueryParameters(path.getQuery());

          try {

            List<Constraints> constraintsList = new ArrayList<>();

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
              value = new Form(URIUtils.parseQueryParameters(form));
            } else {
              // POJO assumed
              try {
                value = JsonbBuilder.create().fromJson(exchange.getRequestBody(), requestBodyModelType);
              } catch (JsonbException | NoSuchElementException ex) {
                respond(405, "Invalid request body.".getBytes(), exchange);
                return;
              }
              constraintsList.add(((Model) value).constraints());
            }

            constraintsList.add(httpEndpoint.get().constraints(
                (Model) value, pathVariables, queryParameters, exchange.getRequestHeaders()
            ));

            Constraints[] constraints = constraintsList.stream().toArray(Constraints[]::new);

            ConstraintViolations violations = Validator.validate(constraints);
            if (!violations.getList().isEmpty()) {
              respond(400, "Constraint violations detected.".getBytes(), exchange);
              return;
            }

          } catch (JsonbException e) {
            respond(405, "Invalid request body.".getBytes(), exchange);
            return;
          }

          Request request = new Request((Identity) exchange.getPrincipal(), exchange.getRequestHeaders(), value, pathVariables, queryParameters, exchange.getRequestURI());

          // Global before interceptor
          List<BeforeInterceptor> globalBeforeInterceptors = Instance.providersOf(BeforeInterceptor.class);
          globalBeforeInterceptors.sort(Comparator.comparing(BeforeInterceptor::order));

          for (BeforeInterceptor globalBeforeInterceptor : globalBeforeInterceptors) {
            globalBeforeInterceptor.intercept(request);
          }

          // Local http endpoint before interceptor
          httpEndpoint.get().before(request);

          // Creating response
          Object response = null;
          try {
            response = httpEndpoint.get().response(request);
          } catch (Exception e) {
            ExceptionInfo exceptionInfo = httpEndpoint.get().onException(e);

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

            respond(exceptionInfo.getHttpStatus(), exceptionInfo.getDescription().getBytes(), exchange);
            return;
          }

          // Global after interceptor
          List<AfterInterceptor> globalAfterInterceptors = Instance.providersOf(AfterInterceptor.class);
          globalAfterInterceptors.sort(Comparator.comparing(AfterInterceptor::order));

          for (AfterInterceptor globalAfterInterceptor : globalAfterInterceptors) {
            globalAfterInterceptor.intercept(request, response);
          }

          // Local http endpoint before interceptor
          httpEndpoint.get().after(request, (Model) response);

          if (Objects.nonNull(response)) {

            byte[] responseTransformed;

            if (response instanceof Empty) {
              exchange.getResponseHeaders().put("Content-Type", Arrays.asList(MediaType.ALL));
              responseTransformed = "".getBytes();
            } else if (response instanceof Json) {
              exchange.getResponseHeaders().put("Content-Type", Arrays.asList(MediaType.APPLICATION_JSON));
              responseTransformed = ((Json) response).getValue().toString().getBytes();
            } else if (response instanceof Text) {
              exchange.getResponseHeaders().put("Content-Type", Arrays.asList(MediaType.TEXT_PLAIN));
              responseTransformed = ((Text) response).getValue().getBytes();
            } else if (response instanceof Html) {
              exchange.getResponseHeaders().put("Content-Type", Arrays.asList(MediaType.TEXT_HTML));
              responseTransformed = ((Html) response).getValue().getBytes();
            } else if (response instanceof View) {
              exchange.getResponseHeaders().put("Content-Type", Arrays.asList(MediaType.TEXT_HTML));

              View view = (View) response;

              if (Objects.nonNull(Instance.of(ViewEngine.class))) {
                responseTransformed = view.getValue().getBytes();
              } else {
                respond(500, "Could not resolve view.".getBytes(), exchange);
                return;
              }

            } else if (response instanceof StaticResource) {
              StaticResource staticResource = (StaticResource) response;

              exchange.getResponseHeaders().put("Content-Type", Arrays.asList(staticResource.getMediaType()));

              try (InputStream is = staticResource.getValue()) {
                responseTransformed = is.readAllBytes();
              }

            } else {
              // POJO assumed
              exchange.getResponseHeaders().put("Content-Type", Arrays.asList(MediaType.APPLICATION_JSON));
              responseTransformed = JsonbBuilder.create().toJson((response)).getBytes();
            }

            respond(httpEndpoint.get().httpStatus(), responseTransformed, exchange);
          } else {
            URI redirectUri = httpEndpoint.get().redirect();
            if (Objects.nonNull(redirectUri)) {
              exchange.getResponseHeaders().add("location", redirectUri.toString());
              respond(302, null, exchange);
            }
          }

        } else {
          respond(404, null, exchange);
        }
      } catch (IOException e) {
        System.getLogger(this.getClass().getName()).log(System.Logger.Level.ERROR, e);
      }
    });
  }

  private void respond(int httpStatus, byte[] message, HttpExchange httpExchange) throws IOException {
    httpExchange.sendResponseHeaders(httpStatus, Objects.isNull(message) ? 0 : message.length);
    if (Objects.nonNull(message)) {
      httpExchange.getResponseBody().write(message);
      httpExchange.getResponseBody().flush();
    }
    httpExchange.close();
  }

}
