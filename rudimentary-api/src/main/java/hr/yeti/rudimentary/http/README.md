## Http

### Introduction
`hr.yeti.rudimentary.http.spi.HttpEndpoint` is probably the most important SPI for any Rudimentary developer. This is the class to implement to expose some functionality over http.  

### Idea & design
This SPI is designed in such a way that it only allows one http method implementation per class. This is quite different to what we see in most of today's modern frameworks. This makes code more readable and testable. Also, since Rudimentary uses Java  module system it is super easy to include any number of modules consisting of just one HttpEndpoint implementation to the Rudimentary runtime.

### HttpEndpoint
This is the main SPI for any Rudimentary developer. 
Through this SPI you can expose some business functionality over http.
You can have as many different HttpEndpoint implementations as you want and you can register them in src/main/resources/META-INF/services/hr.yeti.rudimentary.http.spi.HttpEndpoint. This however, *rudimentary-maven-plugin* automatically does for you.

#### Defining new http endpoint is as simple as:
```java
// Create custom endpoint which receives Empty request body and return Text response
public class CustomEndpoint implements HttpEndpoint<Empty, Text> {
    
    @Override
    public Text response(Request<Empty> request) {
        return new Text("Hello World!");
    }
}
```

#### Setting endpoint http method
Default http method is set to GET. To set new http method just override *httpMethod* method.
```java
@Override
public HttpMethod httpMethod() {
    return HttpMethod.POST;
}
```

#### Setting endpoint http status
Default http status which endpoint returns is set to 200. To set new http statuse override *httpStatus* method.
```java
@Override
public int httpStatus() {
    return 201;
}
```

#### Setting http endpoint path
Default http uri path is set to class name with first letter as lower case.
If class name was `CustomHttpEndpoint` the default path would then be `customHttpEndpoint`.
To set new http endpoint uri path override *path* method.
```java
@Override
public URI path() {
    return URI.create("/custom");
}
```
If you would like to have parameter as part of you uri path define path as:
```java
@Override
public URI path() {
    return URI.create("/custom/:id"); // Define id as path variable. You can multiple path variables, e.g. /custom/:id1/dummy/:id2
}

@Override
public Text response(Request<Empty> request) {
    return new Text(request.getPathVariables().get("id"))); // Send value of id path variable as response
}

```

#### Returning http response
To return something as a http response override *response* method.
```java
@Override
public Text response(Request<Empty> request) {
    return new Text("Hello World!");
}
```
#### Returning http headers
In order to set which http headers to return, override *responseHttpHeaders* method.
The same can also be achived through *response* method by using `request.getHttpExchange().getResponseHeaders()`.
```java
@Override
public Headers responseHttpHeaders(Headers requestHeaders) {
    Headers newHeaders = new Headers();
    newHeaders.add("key", "value");
    return newHeaders;
}
```
#### Handle exceptions
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
#### Before interceptor
To execute custom piece of logic before `response` method execution override `before` method.
```java
@Override
public void before(Request<Text> request) {
  // Do something before response method processing
}
```
#### After interceptor
To execute custom piece of logic after `response` method execution override `after` method.
```java
@Override
public void after(Request<Text> request, Text response) {
  // Do something after response method processing
}
```
#### Logging inside http endpoint
Convenient method for logger access is provided via `logger` method.
```java
@Override
public Text response(Request<Text> request) {
    logger().log(System.Logger.Level.INFO, "Some message.");
    return new Text("Hello World!");
}
```
#### Constraint validations
Http endpoint provides convenient way to define constraints for parts of incoming http request.
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




