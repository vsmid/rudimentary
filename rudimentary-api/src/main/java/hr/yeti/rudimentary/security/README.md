# Security
Applications and services in most cases require some sort of user access management. Rudimentary for now offers basic and login form authentication mechanism(more to come in the future). You can also implement your own authentication mechanism using SPIs provided by the framework.

## AuthMechanism
This is the mechanism which performs user authentication against resource which holds user information such as database etc.
Rudimentary framework for now provides two authentication mechanisms but also offers a simple way to create a custom one.

### BasicAuthMechanism
To enable basic authentication mechanism set `security.basic.enabled` property to true.

### LoginFormAuthMechanism
To enable basic authentication mechanism set `security.loginForm.enabled` property to true.
This mechanism automatically starts user session on successful authentication.

#### Configuring login form
This is done by setting below listed Rudimentary properties. Values shown are used by default implementation but you can overide them at any time.
```properties
security.loginForm.loginURI=/_login # Login uri
security.loginForm.redirectAfterSuccessfulLoginURI=_redirectAfterSuccessfulLogin # Dedicated endpoint which redirects to landing view and performs some logic before actual redirect to landing view uri
security.loginForm.landingViewURI= # Landing view uri (if not set deep linking is used)
security.loginForm.usernameFieldName=username # Username field name attribute value
security.loginForm.passwordFieldName=password # Password field name attribute value
```
### Creating custom authentication mechanism
To create a custom authentication mechanism extend `hr.yeti.rudimentary.security.spi.AuthMechanism` class.
Authentication mechanism should always depend on `IdentityStore` and should always call `super.initialize()` in `initialize` method.
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

  @Override
  public Class[] dependsOn() {
    return new Class[]{ IdentityStore.class };
  }
  
  @Override
  public void initialize() {
    super.initialize();
    ...
  }
  
}
```
You can have only one AuthMechanism you can register it in *src/main/resources/META-INF/services/hr.yeti.rudimentary.security.spi.AuthMechanism*. 

**Hint** - take a look at how `BasicAuthMechanism` is implemented.

### Setting uris which require authentication
This done by setting `security.urisRequiringAuthentication` property.
You can user regular expression to define uris.
 
### Setting uris which don't require authentication
This done by setting `security.urisNotRequiringAuthentication` property.
You can user regular expression to define uris.

### Starting authenticated session
Sometimes you would like to avoid triggering authentication against the resource for subsequent requests.
For example, `LoginFormAuthMechanism` does that automatically. 
In general, you would not use this for stateless web services and this is typically used for web applications which store some information into session.

To start authenticated session on first successful authentication set `security.startAuthenticatedSessionOnSuccessfulAuth` property to true. Behind the scenes this will generate RSID http cookie by which user requests are recognized.

## IdentityStore
`hr.yeti.rudimentary.security.spi.IdentityStore` SPI is used to validate user credentials and to retrieve user's identity.
Identity store should always be provided otherwise authentication machanism will not work.

### Embedded identity store
Rudimentary for now provides only embedded identity store. You can enable it by setting
`security.identityStore.embedded.enabled`property to true.

#### Configuring embedded identity store identities
This is done by setting `security.identityStore.embedded.identities`property.
The format used is: *username*:*password*:*groups*:*roles*:*details* where groups and roles are values separated with comma and details is key=value separated with comma.
```properties
security.identityStore.embedded.identities=\
lena:pass:admins:rookie:email=vsmid@gmail.com,city=Zagreb;\
mmeglic:pass::read,write;\
```
### Creating custom identity store
Creating custom identity store is done by implementing `hr.yeti.rudimentary.security.spi.IdentityStore` interface.
For now only one provider is allowed and it can be registered in *src/main/resources/META-INF/services/hr.yeti.rudimentary.security.spi.IdentityStore* file. Federated identity store will be added in the future.

#### Identity store interface
* public boolean validate(Credential credential) - implement how user credentials are validated agains a resource. Intended use is in overriden `AuthMechanism#doAuth` method.
* public Identity<?> getIdentity(HttpPrincipal principal) - implement how user info is retrieved. Intended use is in overriden  `IdentityStore#getIdentity` method.

**Hint** - take a look at how `EmbeddedIdentityStore` is implemented.

## IdentityDetails
This is used when you wish to add custom details about authenticated user, primarily when overriding `IdentityStore#getIdentity` method. 
This is not a mandatory SPI and is more of a declarative way of providing user details. 

To create a custom identity details provider implement `hr.yeti.rudimentary.security.spi.IdentityDetails` interface.
For now only one provider is allowed and it can be registered in *src/main/resources/META-INF/services/hr.yeti.rudimentary.security.spi.IdentityDetails* file.

## Authorizations
Authorizations are set on `HttpEndpoint` level by overriding `authorizations` method.
Rudimentary framework provides a helper class `hr.yeti.rudimentary.security.Authorization` containing commonly used authorization rules but you can define custom rules as well.

* allow access to all users - `Authorization.ALLOW_ALL`
* deny to all users - `Authorization.DENY_ALL`
* alow access to certain roles - `Authorization.rolesAllowed(String... roles)`
* allow access to certains groups -  `Authorization.groupsAllowed(String... groups)`

```java
@Override
public Predicate<Request> authorizations() {
    return Authorization.rolesAllowed("rookie");
}
```
## Configuring CORS
To configure CORS use these configuration properties.
```properties
security.cors.enabled=false # Enable CORS
security.cors.allowOrigin=* # Comma separated list of origins allowed
security.cors.allowHeaders=origin, accept, content-type # Comma separated list of http headers allowed
security.cors.exposeHeaders=location, info # Comma separated list of exposed http headers as part of the response
security.cors.allowCredentials=true # Indicates whether or not cross-site Access-Control requests should be made using credentials such as cookies, authorization headers or TLS client certificates
security.cors.allowMethods=GET, POST, PUT, DELETE, OPTIONS, HEAD, TRACE # List of allowed http methods
```

## Configuring CSRF protection
To configure CSRF protection use these configuration properties.
```properties
security.csrf.enabled=false # Enable CSRF protection
security.csrf.tokenHttpHeaderName=X-CSRF-TOKEN # The name of http header which must be sent on each http request
security.csrf.tokenCookieName=CSRF-TOKEN # The name of http cookie which holds CSRF token value to be sent in http header
```
