package hr.yeti.rudimentary.server;

import hr.yeti.rudimentary.server._Models;
import com.sun.net.httpserver.Headers;
import hr.yeti.rudimentary.events.EventPublisher;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.ByteStream;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Form;
import hr.yeti.rudimentary.http.content.Html;
import hr.yeti.rudimentary.http.content.Json;
import hr.yeti.rudimentary.http.content.Redirect;
import hr.yeti.rudimentary.http.content.StaticResource;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.mvc.spi.ViewEndpoint;
import hr.yeti.rudimentary.server.resources.ClasspathResource;
import hr.yeti.rudimentary.validation.Constraint;
import hr.yeti.rudimentary.validation.Constraints;
import java.net.URI;
import java.util.Map;
import java.util.regex.Pattern;

public class _HttpEndpoints {

    public static class EmptyResponseEndpoint implements HttpEndpoint<Empty, Empty> {

        @Override
        public URI path() {
            return URI.create("emptyresponse");
        }

        @Override
        public Empty response(Request<Empty> request) {
            return new Empty();
        }

    }

    public static class EmptyRequestEndpoint implements HttpEndpoint<Empty, Empty> {

        @Override
        public URI path() {
            return URI.create("emptyrequest");
        }

        @Override
        public Empty response(Request<Empty> request) {
            return new Empty();
        }

    }

    public static class TextRequestEndpoint implements HttpEndpoint<Text, Empty> {

        @Override
        public URI path() {
            return URI.create("textrequest");
        }

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.POST;
        }

        @Override
        public Empty response(Request<Text> request) {
            return new Empty();
        }

    }

    public static class TextResponseEndpoint implements HttpEndpoint<Empty, Text> {

        @Override
        public URI path() {
            return URI.create("textresponse");
        }

        @Override
        public Text response(Request<Empty> request) {
            return new Text("hello world.");
        }

    }

    public static class PojoResponseEndpoint implements HttpEndpoint<Empty, _Models._CarModel> {

        @Override
        public URI path() {
            return URI.create("pojoresponse");
        }

        @Override
        public _Models._CarModel response(Request<Empty> request) {
            return new _Models._CarModel("Mazda");
        }

    }

    public static class PojoRequestEndpoint implements HttpEndpoint<_Models._CarModel, Empty> {

        @Override
        public URI path() {
            return URI.create("pojorequest");
        }

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.POST;
        }

        @Override
        public Empty response(Request<_Models._CarModel> request) {
            return new Empty();
        }

    }

    public static class HtmlResponseEndpoint implements HttpEndpoint<Empty, Html> {

        @Override
        public URI path() {
            return URI.create("htmlresponse");
        }

        @Override
        public Html response(Request<Empty> request) {
            return new Html("<html></html>");
        }

    }

    public static class HtmlRequestEndpoint implements HttpEndpoint<Html, Empty> {

        @Override
        public URI path() {
            return URI.create("htmlrequest");
        }

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.POST;
        }

        @Override
        public Empty response(Request<Html> request) {
            return new Empty();
        }

    }

    public static class JsonResponseEndpoint implements HttpEndpoint<Empty, Json> {

        @Override
        public URI path() {
            return URI.create("jsonresponse");
        }

        @Override
        public Json response(Request<Empty> request) {
            return new Json(
                Map.of("name", "lena")
            );
        }

    }

    public static class JsonRequestEndpoint implements HttpEndpoint<Json, Empty> {

        @Override
        public URI path() {
            return URI.create("jsonrequest");
        }

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.POST;
        }

        @Override
        public Empty response(Request<Json> request) {
            return new Empty();
        }

    }

    public static class ByteStreamResponseEndpoint implements HttpEndpoint<ByteStream, ByteStream> {

        @Override
        public URI path() {
            return URI.create("bytestreamresponse");
        }

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.POST;
        }

        @Override
        public ByteStream response(Request<ByteStream> request) {
            return new ByteStream((outputStream) -> {
                int c;
                while ((c = request.getBody().getValue().read()) != -1) {
                    outputStream.write((char) c);
                    outputStream.flush();
                }
            });
        }

    }

    public static class ByteStreamRequestEndpoint implements HttpEndpoint<ByteStream, Empty> {

        @Override
        public URI path() {
            return URI.create("bytestreamrequest");
        }

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.POST;
        }

        @Override
        public Empty response(Request<ByteStream> request) {
            return new Empty();
        }

    }

    public static class FormRequestEndpoin implements HttpEndpoint<Form, Empty> {

        @Override
        public URI path() {
            return URI.create("formrequest");
        }

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.POST;
        }

        @Override
        public Empty response(Request<Form> request) {
            return new Empty();
        }

    }

    public static class ViewStaticResponseEndpoint implements ViewEndpoint<Empty> {

        @Override
        public URI path() {
            return URI.create("staticviewresponse");
        }

        @Override
        public View response(Request<Empty> request) {
            return new View("yeti.html", null);
        }

    }

    public static class StaticResourceResponseEndpoint implements HttpEndpoint<Empty, StaticResource> {

        @Override
        public URI path() {
            return URI.create("staticresourceresponse");
        }

        @Override
        public StaticResource response(Request<Empty> request) {
            return new StaticResource(new ClasspathResource("static/yeti.js").get(), MediaType.APPLICATION_JAVASCRIPT);
        }

    }

    public static class RedirectResponseEndpoint implements HttpEndpoint<Empty, Redirect> {

        @Override
        public URI path() {
            return URI.create("redirectresponse");
        }

        @Override
        public Redirect response(Request<Empty> request) {
            return new Redirect(URI.create("redirected"), 302);
        }

    }

    public static class PojoConstrainedEndpoint implements HttpEndpoint<Text, Empty> {

        final Pattern onlyLetters = Pattern.compile("[a-zA-Z]+");

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.POST;
        }

        @Override
        public URI path() {
            return URI.create("pojoconstrainedrequest");
        }

        @Override
        public Constraints constraints(Text body, Map<String, String> pathVariables, Map<String, String> queryParameters, Headers httpHeaders) {
            return new Constraints() {
                {
                    o(body.getValue(), Constraint.NOT_EMPTY, Constraint.REGEX(onlyLetters));
                }
            };
        }

        @Override
        public Empty response(Request<Text> request) {
            return new Empty();
        }

    }

    public static class JsonArrayResponseEndpoint implements HttpEndpoint<Empty, Json> {

        @Override
        public URI path() {
            return URI.create("jsonarrayresponse");
        }

        @Override
        public Json response(Request<Empty> request) {
            return new Json(
                javax.json.Json.createArrayBuilder().add("a").build()
            );
        }

    }

    public static class GlobalExceptionHandlerEndpoint implements HttpEndpoint<Empty, Empty> {

        @Override
        public URI path() {
            return URI.create("globalexceptionhandlerrequest");
        }

        @Override
        public Empty response(Request<Empty> request) {
            throw new IllegalArgumentException("Damn!");
        }

    }

    public static class ConstrainedByHttpEndpointEndpoint implements HttpEndpoint<Text, Empty> {

        final Pattern onlyLetters = Pattern.compile("[a-zA-Z]+");

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.POST;
        }

        @Override
        public URI path() {
            return URI.create("constrainedrequest");
        }

        @Override
        public Constraints constraints(Text body, Map<String, String> pathVariables, Map<String, String> queryParameters, Headers httpHeaders) {
            return new Constraints() {
                {
                    o(body.getValue(), Constraint.NOT_EMPTY, Constraint.REGEX(onlyLetters));
                }
            };
        }

        @Override
        public Empty response(Request<Text> request) {
            return new Empty();
        }

    }

    public static class ConstrainedByPojoEndpoint implements HttpEndpoint<_Models._PojoConstrained, Empty> {

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.POST;
        }

        @Override
        public URI path() {
            return URI.create("pojoconstrainedrequest");
        }

        @Override
        public Empty response(Request<_Models._PojoConstrained> request) {
            return new Empty();
        }

    }

    public static class BeforeInterceptorForURIEndpoint implements HttpEndpoint<Empty, Empty> {

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.GET;
        }

        @Override
        public URI path() {
            return URI.create("uriinterceptbefore");
        }

        @Override
        public Empty response(Request<Empty> request) {
            return new Empty();
        }

    }

    public static class AfterInterceptorForURIEndpoint implements HttpEndpoint<Empty, Empty> {

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.GET;
        }

        @Override
        public URI path() {
            return URI.create("uriinterceptafter");
        }

        @Override
        public Empty response(Request<Empty> request) {
            return new Empty();
        }

    }

    public static class HttpEndpointWithBeforeInterceptEndpoint implements HttpEndpoint<Empty, Empty> {

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.GET;
        }

        @Override
        public URI path() {
            return URI.create("uriinterceptwithbefore");
        }

        @Override
        public void before(Request<Empty> request) {
            System.out.print("2");
        }

        @Override
        public Empty response(Request<Empty> request) {
            return new Empty();
        }

    }

    public static class HttpEndpointWithAfterInterceptEndpoint implements HttpEndpoint<Empty, Empty> {

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.GET;
        }

        @Override
        public URI path() {
            return URI.create("uriinterceptwithafter");
        }

        @Override
        public void before(Request<Empty> request) {
            System.out.print("2");
        }

        @Override
        public Empty response(Request<Empty> request) {
            return new Empty();
        }

    }

    public static class BlogPostEventPublishingEndpoint implements HttpEndpoint<_Models.BlogPost, Text> {

        public String text;
        
        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.POST;
        }

        @Override
        public URI path() {
            return URI.create("blogpost");
        }

        @Override
        public Text response(Request<_Models.BlogPost> request) {            
            if (request.getQueryParameters().get("type").equals("async")) {
                request.getBody().publish(EventPublisher.Type.ASYNC);
            } else {
                request.getBody().publish(EventPublisher.Type.SYNC);
            }
            text = request.getBody().text;
            return new Text(request.getBody().text);
        }

    }
}
