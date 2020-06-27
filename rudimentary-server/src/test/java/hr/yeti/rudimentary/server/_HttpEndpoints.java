package hr.yeti.rudimentary.server;

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
import hr.yeti.rudimentary.sql.Sql;
import hr.yeti.rudimentary.sql.spi.BasicDataSource;
import hr.yeti.rudimentary.validation.Constraint;
import hr.yeti.rudimentary.validation.Constraints;
import java.util.Map;
import java.util.regex.Pattern;

public class _HttpEndpoints {

    public static class EmptyResponseEndpoint implements HttpEndpoint<Empty, Empty> {

        @Override
        public String path() {
            return "emptyresponse";
        }

        @Override
        public Empty response(Request<Empty> request) {
            return new Empty();
        }

    }

    public static class EmptyRequestEndpoint implements HttpEndpoint<Empty, Empty> {

        @Override
        public String path() {
            return "emptyrequest";
        }

        @Override
        public Empty response(Request<Empty> request) {
            return new Empty();
        }

    }

    public static class TextRequestEndpoint implements HttpEndpoint<Text, Empty> {

        @Override
        public String path() {
            return "textrequest";
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
        public String path() {
            return "textresponse";
        }

        @Override
        public Text response(Request<Empty> request) {
            return new Text("hello world.");
        }

    }

    public static class PojoResponseEndpoint implements HttpEndpoint<Empty, _Models._CarModel> {

        @Override
        public String path() {
            return "pojoresponse";
        }

        @Override
        public _Models._CarModel response(Request<Empty> request) {
            return new _Models._CarModel("Mazda");
        }

    }

    public static class PojoRequestEndpoint implements HttpEndpoint<_Models._CarModel, Empty> {

        @Override
        public String path() {
            return "pojorequest";
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
        public String path() {
            return "htmlresponse";
        }

        @Override
        public Html response(Request<Empty> request) {
            return new Html("<html></html>");
        }

    }

    public static class HtmlRequestEndpoint implements HttpEndpoint<Html, Empty> {

        @Override
        public String path() {
            return "htmlrequest";
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
        public String path() {
            return "jsonresponse";
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
        public String path() {
            return "jsonrequest";
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
        public String path() {
            return "bytestreamresponse";
        }

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.POST;
        }

        @Override
        public ByteStream response(Request<ByteStream> request) {
            return new ByteStream((outputStream) -> {
                int c;
                while ((c = request.getBody().get().read()) != -1) {
                    outputStream.write((char) c);
                    outputStream.flush();
                }
            });
        }

    }

    public static class ByteStreamRequestEndpoint implements HttpEndpoint<ByteStream, Empty> {

        @Override
        public String path() {
            return "bytestreamrequest";
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
        public String path() {
            return "formrequest";
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
        public String path() {
            return "staticviewresponse";
        }

        @Override
        public View response(Request<Empty> request) {
            return new View("yeti.html", null);
        }

    }

    public static class StaticResourceResponseEndpoint implements HttpEndpoint<Empty, StaticResource> {

        @Override
        public String path() {
            return "staticresourceresponse";
        }

        @Override
        public StaticResource response(Request<Empty> request) {
            return new StaticResource(new ClasspathResource("static/yeti.js").get(), MediaType.APPLICATION_JAVASCRIPT);
        }

    }

    public static class RedirectResponseEndpoint implements HttpEndpoint<Empty, Redirect> {

        @Override
        public String path() {
            return "redirectresponse";
        }

        @Override
        public Redirect response(Request<Empty> request) {
            return new Redirect("redirected", 302);
        }

    }

    public static class PojoConstrainedEndpoint implements HttpEndpoint<Text, Empty> {

        final Pattern onlyLetters = Pattern.compile("[a-zA-Z]+");

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.POST;
        }

        @Override
        public String path() {
            return "pojoconstrainedrequest";
        }

        @Override
        public Constraints constraints(Request<Text> request) {
            return new Constraints() {
                {
                    o(request.getBody().get(), Constraint.NOT_EMPTY, Constraint.REGEX(onlyLetters));
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
        public String path() {
            return "jsonarrayresponse";
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
        public String path() {
            return "globalexceptionhandlerrequest";
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
        public String path() {
            return "constrainedrequest";
        }

        @Override
        public Constraints constraints(Request<Text> request) {
            return new Constraints() {
                {
                    o(request.getBody().get(), Constraint.NOT_EMPTY, Constraint.REGEX(onlyLetters));
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
        public String path() {
            return "pojoconstrainedrequest";
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
        public String path() {
            return "uriinterceptbefore";
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
        public String path() {
            return "uriinterceptafter";
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
        public String path() {
            return "uriinterceptwithbefore";
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
        public String path() {
            return "uriinterceptwithafter";
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
        public String path() {
            return "blogpost";
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

    public static class SqlEndpoint implements HttpEndpoint<Empty, Json> {

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.GET;
        }

        @Override
        public String path() {
            return "sql";
        }

        @Override
        public Json response(Request<Empty> request) {
            return new Json(Sql.query().rows("SELECT * FROM USERS;"));
        }

        @Override
        public void initialize() {
            Sql.tx((sql) -> {
                sql.update("DROP TABLE IF EXISTS USERS;");
                sql.update("CREATE TABLE USERS(ID, NAME);");
                sql.update("INSERT INTO USERS(ID, NAME) VALUES(?, ?);", 1, "Lena");
                sql.update("INSERT INTO USERS(ID, NAME) VALUES(?, ?);", 2, "Martina");
                return null;
            });
        }

        @Override
        public Class[] dependsOn() {
            return new Class[]{ BasicDataSource.class };
        }

    }

    public static class AuthMechanismEndpoint implements HttpEndpoint<Empty, Text> {

        @Override
        public HttpMethod httpMethod() {
            return HttpMethod.GET;
        }

        @Override
        public String path() {
            return "auth";
        }

        @Override
        public Text response(Request<Empty> request) {
            return new Text(request.getIdentity().toString());
        }

    }

}
