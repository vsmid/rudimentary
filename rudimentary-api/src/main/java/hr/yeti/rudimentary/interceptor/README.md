## Interceptors

### Introduction
Sometimes you want to apply a piece of logic before or after some operation. Rudimentary offers simple way to do that through
`hr.yeti.rudimentary.interceptor.spi.BeforeInterceptor` and `hr.yeti.rudimentary.interceptor.spi.AfterInterceptor`.

### Interceptor scope
Scope for both before and after interceptor is global. That means that interceptors will be executed on the `HttpEndpoint` level. Simply put, before or after `HttpEndpoint#response` method. If interceptor's `Interceptor#applyToURI` matches endpoint's `HttpEndpoint#path` intreceptor will be executed. 

Important things to note is that global before interceptors will always be executed before `HttpEndpoint#before` method and global after interceptors will always be executed before `HttpEndpoint#after` method.

### Creating interceptor
To create your own before interceptor simply implement `hr.yeti.rudimentary.interceptor.spi.BeforeInterceptor`. For after interceptor implement `hr.yeti.rudimentary.interceptor.spi.AfterInterceptor`.

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

#### Setting interceptor order
You can set interceptor order by overriding `order` method.
```java
@Override
public int order() {
    return 1;
}
```

#### Setting interceptor URI (scoping)
You can set to which uri's will interceptor be applied by overriding `applyToURI` method. Default value is set to `.*` to match all URIs since it is a global interceptor. You can use regular expression to define custom URIs to which interceptor will be applied to.
```java
@Override
public String applyToURI() {
    return "/_health";
}
```

