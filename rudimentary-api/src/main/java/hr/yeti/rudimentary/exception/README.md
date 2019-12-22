# Exception handling
Rudimentary offers simple way of defining either global exception handler or per `HttpEndpoint`.
Both global exception handler and http endpoint exception handler will handle exceptions which occur during response construction.  

## Global exception handler
You can have only one global exception handler per application/service.
You can create one by implementing `hr.yeti.rudimentary.exception.spi.ExceptionHandler` and registering it in
`src/main/resources/META-INF/services/hr.yeti.rudimentary.exception.spi.ExceptionHandler` file like any other Java service provider. This however, `rudimentary-maven-plugin` already automatically does for you.

## HttpEndpoint exception handler
This handler takes precedance over global exception handler if such exists.
You can add exception handler to each `HttpEndpoint` by overriding `HttpEndpoint#onException` method.
```java
public class OnExceptionEndpoint implements HttpEndpoint<Json, Text> {
  
  ...
  
  @Override
  public ExceptionInfo onException(Exception e) {
      if(e instanceof IllegalArgumentException) {
          return new ExceptionInfo(400, "Illegal.");
      }
      
      return ExceptionInfo.defaultExceptionInfo();
  }
}
```

