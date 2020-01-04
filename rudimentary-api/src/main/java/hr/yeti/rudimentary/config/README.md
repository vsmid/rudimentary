# Configuration

## Introduction
Makes dealing with application/service configuration a little bit easier. 
A set of loaders is provided to enable configuration loading from basically any source.
Also, a convenient set of property value converters is available to make value transformation a breeze.

## Configuration properties loading hierarchy
Property value is resolved based on the following order(top to bottom, top has the highest priority):

* System property
* Environment property
* Properties loaded using one of the `Config.load(...)` methods provided
* Default value provided on the fly `e.g. new ConfigProperty("val", "defaultValue")`

## Accessing configuration properties
There are multiple ways of accessing property values depending on your needs and preferred style.

```java
  Config.provider().property("name"); // Static access, returns ConfigProperty instance for property named 'name'
  Config.provider().property("name", "Lena"); // Static access which also sets default value if no property is found within provider for property named 'name', returns ConfigProperty instance
  Config.provider().value("name"); // Static access, returns String value
  Config.provider().getProperties(); // Gets all properties as java.util.Properties
  Config.provider().getPropertiesByPrefix("group", false); // Gets all properties as java.util.Properties which start with 'group' string. Boolean parameter decides whether to keep group prefix name or not. Convenient if you need to pass a group of properties but you do not know all the property names in advance (e.g. javax.mail.Session object creation).
  new ConfigProperty("name"); // Class level property named 'name'
  new ConfigProperty("name", "Lena"); // Class level property named 'name' which also sets default value if no property is found within provider
```

## Property value converters
ConfigProperty type comes with a set of default value converters.

```java
  Config.provider().property("name").value(); // Returns value as String
  new ConfigProperty("name").value(); // Returns value as String, same as above example
  Config.provider().property("name").toString(); // Returns value as String
  Config.provider().property("age").asInt(); // Returns value as Integer
  Config.provider().property("long").asLong(); // Returns value as Long
  Config.provider().property("truth").asBoolean; // Returns value as Boolean
  Config.provider().property("path").asPath(); // Either a/b/c or a,b,c value format
  Config.provider().property("map").asMap(); // Requires k=v comma sepatared values format e.g. k1=v1,k2=v2,k3=v3
  Config.provider().property("url").asURL(); // Returns value as URL
  Config.provider().property("uri").asURI(); // Returns value as URI
  Config.provider().property("array").asArray(); // Requires comma separated values format, e.q. a,b,c,d
  Config.provider().property("name").transform(String::toUpperCase); // Custom transformation on the fly
```

## Configuration provider
Configuration provider is a class which extends `hr.yeti.rudimentary.config.spi.Config` class.
By overriding its `initialize()` method you can define how the properties will be loaded(from the database, file, remote URL etc.).

Configuration providers should be registered by writing canonical class name of the provider to the `src/main/java/META-INF/services/hr.yeti.rudimentary.config.spi.Config` file.
Fortunately for you, Rudimentary already comes with Maven plugin (`rudimentary-maven-plugin`) which does that automatically for you.

### Default configuration provider
By default, Rudimentary provides default configuration provider in the form of `hr.yeti.rudimentary.server.config.DefaultConfigProvider`. This provider loads configuration from the property file located in `src/main/resources/config.properties`. Format used is a classic *.properties* file format, meaning each entry is represented as *key=value*. This provider is activated only if it is the only configuration provider available.

### Custom configuration provider
At any time, you can create and register your own, custom configuration provider just by extending `hr.yeti.rudimentary.config.spi.Config` class. Two things you need to remember when implementing your own custom configuration provider:

1. First statement of the custom provider's `initialize()` method should be `super.initialize()` to load default properties
2. Last statement of the custom provider's `initialize()` method should be `seal()` to prevent any further configuration loading

```java
...

public class CustomConfigProvider extends Config {
 
 @Override
 public void initialize() {
  super.initialize();
  
  // TODO Add you own properties loading logic such as database, remote URL etc.
  
  seal();
 }
 
}
```
Only one configuration provider is allowed to be active during runtime. If multiple providers are found, the first one will take precedence. Overriding `primary()` method will have no effect.

### Test configuration provider
Rudimentary provides `hr.yeti.rudimentary.test.ConfigMock` which you can use when writing test cases.

```java
  ConfigMock config = new ConfigMock();
  config.load(
    Map.of("server.port", "8888")
  );
  config.seal();
```

## Configuration endpoint
You can access running application/service configuration properties via `/_config`uri.

## Available configuration properties
```properties
server.port=8888 # Http server port
server.threadPool=25 # Maximum number of threads that can be processed by server at a time
server.stopDelay=0 # Maximum time in seconds to wait until requests have finished
server.ssl.enabled=false # Enable SSL
server.ssl.protocol=TLS # SSL protocol to be used
server.ssl.keyStore= # Key store used for SSL
server.ssl.keyStore.password= # Key store password|
server.ssl.trustStore= # Trust store used for SSL
server.ssl.trustStore.password=  # Trust store password
server.ssl.clientAuth=false # Should client authentication be required
mvc.templatesDir=view # Directory where views are located (under src/main/resources)
mvc.staticResourcesDir=static # Directory where static resources are located (under src/main/resources)
dataSource.enabled=false # Enable JDBC data source
dataSource.driverClassName= # Jdbc driver class name
dataSource.jdbcUrl= # Url string
dataSource.username= # Database username
dataSource.password= # Database password
dataSource.maximumPoolSize=25 # Connection pool size
session.maxConcurrentAllowed=25 # Number of concurrent sessions allowed
session.inactivityPeriodAllowed=1800 # Maximum time allowed between two consequent requests in seconds before session is invalidated
email.smtp.enabled=false # Enable SMTP
email.smtp.pool.minSize=25 # Minimum number of javax.mail.Session in the pool
email.smtp.pool.maxSize=50 # Maximum number of javax.mail.Session in the pool
email.smtp.pool.validationInterval=30 # Time between two checks of pool status
email.smtp.pool.awaitTerminationInterval=15 # Time to wait for tasks to finish before termination
email.smtp.user= # Username
email.smtp.password= # Password
email.smtp.properties.*= # Check possible * values at https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html
security.realm=default # Realm name
security.startAuthenticatedSessionOnSuccessfulAuth=false # Should session be created on successful auth to avoid full auth process on subsequent requests.
security.urisRequiringAuthentication= # Comma separated list of URIs(regex format allowed) for which authentication will be applied
security.urisNotRequiringAuthentication= # Comma separated list of URIs(regex format allowed) for which authentication will not be applied
security.basic.enabled=false # Enable Basic security
security.loginForm.enabled=false # Enable login form security
security.loginForm.loginURI=/_login # Login view URI
security.loginForm.redirectAfterSuccessfulLoginURI=_redirectAfterSuccessfulLogin # Dedicated endpoint which redirects to landing view and performs some logic before actual redirect to landing view uri
security.loginForm.landingViewURI= # Where to land after successful login
security.loginForm.usernameFieldName=username # Login form username input field name
security.loginForm.passwordFieldName=password # Login form password input field name
security.identityStore.embedded.enabled=false # Enable in memory identity store.
security.identityStore.embedded.identities= # In memory identity store users definitions 
security.csrf.enabled=false # Enable CSRF protection
security.csrf.tokenHttpHeaderName=X-CSRF-TOKEN # The name of http header which must be sent on each http request
security.csrf.tokenCookieName=CSRF-TOKEN # The name of http cookie which holds CSRF token value to be sent in http header
security.cors.enabled=false # Enable CORS
security.cors.allowOrigin=* # Comma separated list of origins allowed
security.cors.allowHeaders=origin, accept, content-type # Comma separated list of http headers allowed
security.cors.exposeHeaders=location, info # Comma separated list of exposed http headers as part of the response
security.cors.allowCredentials=true # Indicates whether or not cross-site Access-Control requests should be made using credentials such as cookies, authorization headers or TLS client certificates
security.cors.allowMethods=GET, POST, PUT, DELETE, OPTIONS, HEAD, TRACE # List of allowed http methods
security.cors.maxAge=600 # Indicates how long the results of a preflight request can be cached in seconds
logging.propertiesFile=classpath:server-logging.properties # Logging configuration file
```
