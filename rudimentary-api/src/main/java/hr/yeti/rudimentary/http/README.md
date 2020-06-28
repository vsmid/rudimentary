# Http

### Introduction
`hr.yeti.rudimentary.http.spi.HttpEndpoint` is probably the most important SPI for any Rudimentary developer. This is the class to implement to expose some functionality over http.  

### Idea & design
This SPI is designed in such a way that it only allows one http method implementation per class. This is quite different to what we see in most of today's modern frameworks. This makes code more readable and testable. Also, since Rudimentary uses Java  module system it is super easy to include any number of modules consisting of just one HttpEndpoint implementation to the Rudimentary runtime.

## HttpEndpoint
This is the main SPI for any Rudimentary developer. 
Through this SPI you can expose some business functionality over http.
You can have as many different HttpEndpoint implementations as you want and you can register them in src/main/resources/META-INF/services/hr.yeti.rudimentary.http.spi.HttpEndpoint. This however, *rudimentary-maven-plugin* automatically does for you.

### Creating new http endpoint
```java
// Create custom endpoint which receives Empty request body and returns Text response
// Content types are described further bellow
public class CustomEndpoint implements HttpEndpoint<Empty, Text> {
    
    @Override
    public Text response(Request<Empty> request) {
        return new Text("Hello World!");
    }
}
```

### Setting endpoint http method
Default http method is set to GET. To set new http method just override *httpMethod* method.
```java
@Override
public HttpMethod httpMethod() {
    return HttpMethod.POST;
}
```

### Setting endpoint http status
Default http status which endpoint returns is set to 200. To set new http statuse override *httpStatus* method.
As an alternative, you can also use `Request.setResponseHttpStatus(int status)`.
If both are used, the latter takes precedence.
```java
@Override
public int httpStatus() {
    return 201;
}
```

### Setting http endpoint path
Default http uri path is set to class name with first letter as lower case.
If class name was `CustomHttpEndpoint` the default path would then be `customHttpEndpoint`.
To set new http endpoint uri path override *path* method.
```java
@Override
public String path() {
    return "/custom";
}
```

If you would like to have parameter as part of you uri path define path as:
```java
@Override
public String path() {
    return "/custom/:id"; // Define id as path variable. You can multiple path variables, e.g. /custom/:id1/dummy/:id2
}

@Override
public Text response(Request<Empty> request) {
    return new Text(request.getPathVariables().get("id"))); // Send value of id path variable as response
}
```

### Returning http response
To return something as a http response override *response* method.
```java
@Override
public Text response(Request<Empty> request) {
    return new Text("Hello World!");
}
```
### Returning http headers
In order to set which http headers to return, override *responseHttpHeaders* method.
The same can be achieved in `response` method by using `request.getHttpExchange().getResponseHeaders().add` method or 
by using `Request.addResponseHeader(java.lang.String, java.lang.String)` method. 
Aside from verbosity difference, the latter two options are exactly the same.
`Request.addResponseHeader(java.lang.String, java.lang.String)` and `request.getHttpExchange().getResponseHeaders().add(java.lang.String, java.lang.String)` take precedence over *responseHttpHeaders* method.
```java
@Override
public Headers responseHttpHeaders(Request<I> request, O response) {
    Headers newHeaders = new Headers();
    newHeaders.add("key", "value");
    return newHeaders;
}
```
### Handle exceptions
Sometimes exceptions occur during code execution. If you want to handle exceptions in your endpoint override
`onException` method. This takes priority over global exception handler if you provide one.
```java
@Override
public ExceptionInfo onException(Exception e) {
    if(e instanceof SqlException) {
      return new ExceptionInfo(500, "Something went wrong!");
    }
    return ExceptionInfo.defaultExceptionInfo();
}
```
### Before interceptor
To execute custom piece of logic before `response` method execution override `before` method. This is considered a local interceptor since it will be executed only for this `HttpEndpoint`.
Find out more about global interceptors in [Interceptors](../interceptor/README.md) section.
```java
@Override
public void before(Request<Text> request) {
  // Do something before response method processing
}
```
### After interceptor
To execute custom piece of logic after `response` method execution override `after` method. This is considered a local interceptor since it will be executed only for this `HttpEndpoint`.
Find out more about global interceptors in [Interceptors](../interceptor/README.md) section.
```java
@Override
public void after(Request<Text> request, Text response) {
  // Do something after response method processing
}
```
### Logging inside http endpoint
Convenient method for logger access is provided via `logger` method. For more information about this logger check
[System.Logger](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/System.Logger.html).
```java
@Override
public Text response(Request<Text> request) {
    logger().log(System.Logger.Level.INFO, "Some message.");
    return new Text("Hello World!");
}
```
### Setting constraint validations
Http endpoint provides convenient way to define constraints for parts of incoming http request.
To define custom constraints, override *constraints* method. More information about validation can be found in [Validation](../validation/README.md) section.
```java
@Override
public Constraints constraints(
        Text body,
        Map<String, String> pathVariables,
        Map<String, String> queryParameters,
        Headers httpHeaders
) {
    return new Constraints() {
        {
            o(body.getValue(), Constraint.NOT_NULL, Constraint.NOT_EMPTY);
        }
    };
}
```
### Setting http endpoint authorizations
If you wish to protect http endpoint access with authorization rules override *authorizations* method.
```java
@Override
public Predicate<Request> authorizations() {
    return Authorization.ALLOW_ALL;
}
```
### Setting http endpoint description
To set short endpoint description which will be shown in *apidocs* override *description* method.
```java
@Override
public String description() {
    return "Some description";
}
```

### Request body content types
For now, you can not add additional, custom request body type. It is planned for next release.
* **Empty** - use when you do not expect anything in request body
* **Form** - use when you expect form in request body
* **Html** - use when you expect html in request body
* **Json** - use when you expect json in request body. This type offers mathods for easy type conversion for both json object and json array.
* **Text** - use when you expect text in request body.
* **ByteStream** - use when you expect stream in request body. This could be for example file upload. Multipart is not supported yet.

### Response content types
For now, you can not add additional, custom response type. It is planned for next release.
* **Empty** - use when you do not want to send anything in a response.
* **Html** - use when you want to send html in a response.
* **ByteStream** - use when you want to send byte stream in a response. This could be for example file download.
* **Redirect** - use when you want to perform redirect.
* **StaticResource** - use when you want to send javascript, image etc. in a response. This is already used internally by static resource endpoint.
* **Text** - use when you want to send text in a response.
* **View** - use when you want to send processed view in a response. You can find more on this in [MVC](../mvc/README.md) section.

## API documentation
You access the list and description of all registered `HttpEndpoint` and `ViewEndpoint` providers via `_/apidocs` uri.

## Http session
This functionality provides means to identify the same user across multuple http requests.
During the lifetime of http session you can store information about user and state in session.
Typically, this is used in MCV applications.

Http session will be created the first time you call `Session#acquire` method.
In some cases, http session is created by the framework. Such case would for example be when using login form authentication mechanism.

### RSID
RSID is the name of the http cookie by which user's http session is identified within Rudimentary framework.

### Configuring http session
You can configure http session using Rudimentary configuration options.
```properties
session.maxConcurrentAllowed=25 # How many concurrent sessions are allowed
session.inactivityPeriodAllowed=1800 # After how many seconds will an inactive http session be terminated and invalidated
```
### Accessing http session
* Application wide static access via `Session#acquire`
* HttpEndpoint access via `Request#getSession`

## Http filter
Http filters are used to enrich incoming request or outgoing response or to prevent request from executing depending on some condition. You can have as many filter as you like. 

### Creating http filter
Http filter is created by extending `hr.yeti.rudimentary.http.filter.spi.HttpFilter` and overriding `doFilter` method.
```java
@Override
public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
    ...
}
```
### Http filter ordering
You can set filter order by overriding `order` method.
```java
@Override
public int order() {
    return 100;
}
```
### Registering http filter 
You can register your custom http filter in `src/main/resources/META-INF/services/hr.yeti.rudimentary.http.filter.spi.HttpFilter` file of your application to make it eligible for Java `ServiceLoader`.
This is already done automatically by `rudimentary-maven-plugin`.

## Examples
You can find many HttpEndpoint examples in [rudimentary-demo](../../../../../../../../rudimentary-demo/src/main/java/hr/yeti/rudimentary/demo/endpoint) module.


