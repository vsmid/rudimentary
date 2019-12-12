## Security
Applications and services in most cases require some sort of user access management. Rudimentary for now offers basic and login form authentication mechanism(more to come in the future). You can also implement your own authentication mechanism using SPIs provided by the framework.

### AuthMechanism
This is the mechanism which performs user authentication against resource which holds user information such as database etc.
Rudimentary framework for now provides two authentication mechanisms but also offers a simple way to create a custom one.

#### BasicAuthMechanism
To enable basic authentication mechanism set `security.basic.enabled` property to true.

#### LoginFormAuthMechanism
To enable basic authentication mechanism set `security.loginForm.enabled` property to true.

##### Configuring login form
This is done by setting below listed Rudimentary properties. Values shown are used by default implementation but you can overide them at any time.
```properties
security.loginForm.loginURI=/_login # Login uri
security.loginForm.redirectAfterSuccessfulLoginURI=_redirectAfterSuccessfulLogin # Uri which redirects to landing view and performs some login before actual redirect to landing view uri
security.loginForm.landingViewURI= # Landing view uri (if not set deep linking is used)
security.loginForm.usernameFieldName=username # Username field name attribute value
security.loginForm.passwordFieldName=password # Password field name attribute value
```
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

#### Setting uris which require authentication
This done by setting `security.urisRequiringAuthentication` property.
You can user regular expression to define uris.
 
#### Setting uris which don't require authentication
This done by setting `security.urisNotRequiringAuthentication` property.
You can user regular expression to define uris.

### IdentityStore

#### IdentityStoreDetails
