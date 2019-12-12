## Security
Applications and services in most cases require some sort of user access management. Rudimentary for now offers basic and login form authentication mechanism(more to come in the future). You can also implement your own authentication mechanism using SPIs provided by the framework.

### AuthMechanism
This is the mechanism which performs user authentication against resource which holds user information such as database etc.
Rudimentary framework for now provides two authentication mechanisms but also offers a simple way to create a custom one.

#### BasicAuthMechanism

#### LoginFormAuthMechanism

#### Creating custom authentication mechanism
To create a custom authentication mechanism extend `hr.yeti.rudimentary.security.spi.AuthMechanism` class.
```java
public final class CustomAuthMechanism extends AuthMechanism {

  @Override
  public Result doAuth(HttpExchange exchange) {
    ...
  }
  
  @Override
  public Identity getIdentity(HttpPrincipal principal) {
    ...
  }
  
}
```
You can have only one AuthMechanism you can register them in src/main/resources/META-INF/services/hr.yeti.rudimentary.security.spi.AuthMechanism. 

### IdentityStore

#### IdentityStoreDetails
