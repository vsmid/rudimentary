## Interceptors

### Introduction
Sometimes you want to apply a piece of logic before or after some operation. Rudimentary offers simple way to do that through
`hr.yeti.rudimentary.interceptor.spi.BeforeInterceptor` and `hr.yeti.rudimentary.interceptor.spi.AfterInterceptor`.

### Creating interceptor
To create your own before interceptor simply implement `hr.yeti.rudimentary.interceptor.spi.BeforeInterceptor`. For after interceptor implement `hr.yeti.rudimentary.interceptor.spi.AfterInterceptor`. Put your intreceptor logic inside `intercept` method.

```java
public class LoggingBeforeInterceptor implements BeforeInterceptor {

  @Override
  public int order() {
      return 1;
  }
  @Override
  public void intercept(Request request) {
      System.out.println("Logging interceptor message: " + request.getUri().toString());
  }

}
```

You can have as many different interceptor implementations as you want and you can register them in *src/main/resources/META-INF/services/hr.yeti.rudimentary.interceptor.spi.BeforeInterceptor* and *src/main/resources/META-INF/services/hr.yeti.rudimentary.interceptor.spi.AfterInterceptor*. This however, rudimentary-maven-plugin automatically does for you.

### Setting interceptor order
You can set interceptor order by overriding `order` method.
```java
@Override
public int order() {
    return 1;
}
```

### Setting interceptor scope
Interceptors are always executed before or after `HttpEndpoint#response` method depending on interceptor type. By scope we actually think of set of configured URIs. If interceptor's `Interceptor#applyToURI` matches endpoint's `HttpEndpoint#path` intreceptor will be executed. 

You can set for which URIs will interceptor be executed by overriding `applyToURI` method. Default value is set to `.*` to match all URIs since it is a global interceptor(by deafult executes for all `HttpEndpoint` providers). You can use regular expression to define custom URIs to which interceptor will be applied to.
```java
@Override
public String applyToURI() {
    return "/_health";
}
```
### Relation to `HttpEndpoint#before` and `HttpEndpoint#after` methods
`BeforeInterceptor` interceptors will always be executed before `HttpEndpoint#before` method and `AfterInterceptor` interceptors will always be executed before `HttpEndpoint#after` method.

