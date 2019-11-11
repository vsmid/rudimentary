## Configuration

### Introduction
Makes dealing with application/service configuration a little bit easier. 
A set of loaders is provided to enable configuration loading from basically any source.
Also, a convenient set of property value converters is available to make value transformation a breeze.


### Configuration properties loading hierarchy
Property value is resolved based on the following order(top to bottom, top has the highest priority):

* System property
* Environment property
* Properties loaded using one of the `Config.load(...)` methods provided
* Default value provided on the fly `e.g. new ConfigProperty("val", "defaultValue")`

### Accessing configuration properties
There are multiple ways of accessing property values depending on your needs and preferred style.

```java
  Config.provider().property("name"); // Static access, returns ConfigProperty instance for property named 'name'
  Config.provider().property("name", "Lena"); // Static access which also sets default value if no property is found within provider for property named 'name', returns ConfigProperty instance
  Config.provider().value("name"); // Static access, returns String value
  Config.provider().getProperties(); // Gets all properties as java.util.Properties
  Config.provider().getPropertiesByPrefix("group", false); // Gets all properties as java.util.Properties which start with 'group' string. Boolean parameter decides whether to keep group prefix name or not. Convenient if you need to pass a group of properties but you do not know all the property names in advance (e.g. javax.mail.Session object creation).
  new ConfigProperty("name"); // Class level property named 'Lena'
  new ConfigProperty("name", "Lena"); // Class level property named 'Lena' which also sets default value if no property is found within provider
```

### Configuration provider
Configuration provider is a class which extends `hr.yeti.rudimentary.config.spi.Config` class.
By overriding its `initialize()` method you can define how the properties will be loaded(from the database, file, remote URL etc.).

Configuration providers should be registered by writing canonical class name of the provider to the `src/main/java/META-INF/services/hr.yeti.rudimentary.config.spi.Config` file.
Fortunately for you, Rudimentary already comes with Maven plugin (`rudimentary-maven-plugin`) which does that automatically for you.

At any time, you can create and register your own, custom configuration provider.
Only one configuration provider is allowed to be active during runtime. If multiple providers are found, the first one will take precedence.

Overriding `primary()` method will have no effect.

#### Default configuration provider
By default, Rudimentary provides default configuration provider in the form of `hr.yeti.rudimentary.server.config.DefaultConfigProvider`. This provider loads configuration from the property file located in `src/main/resources/config.properties`. Format used is a classic *.properties* file format, meaning each entry is represented as *key=value*. This provider is activated only if it is the only configuration provider available.

#### Test configuration provider
Rudimentary provides `hr.yeti.rudimentary.test.ConfigMock` which you can use when writing test cases.

```java
  ConfigMock config = new ConfigMock();
  config.load(
    Map.of("server.port", "8888")
  );
  config.seal();
```

### Available configuration properties
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
email.smtp.pool.minSize=25 # Minimim number of javax.mail.Session in the pool
email.smtp.pool.maxSize=50 # Maximum number of javax.mail.Session in the pool
email.smtp.pool.validationInterval=30 # Time between two checks of pool status
email.smtp.pool.awaitTerminationInterval=15 # Time to wait for tasks to finish before termination
email.smtp.user= # Username
email.smtp.password= # Password
email.smtp.properties.*= # Check possible * values at https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html
security.realm=default # Realm name
security.urisRequiringAuthentication= # Comma separated list of URIs(regex format allowed) for which authentication will be applied
security.urisNotRequiringAuthentication= # Comma separated list of URIs(regex format allowed) for which authentication will not be applied
security.basic.enabled=false # Enable Basic security
security.loginForm.enabled=false # Enable login form security
security.loginForm.loginURI=/_login # Login view URI
security.loginForm.redirectAfterSuccessfulLoginURI=_redirectAfterSuccessfulLogin # Http endpoint which will perform redirect to the landing view
security.loginForm.landingViewURI= # Where to land after successful login
security.loginForm.usernameFieldName=username # Login form username input field name
security.loginForm.passwordFieldName=password # Login form password input field name
security.identityStore.embedded.enabled=false # Enable in memory identity store.
security.identityStore.embedded.identities= # In memory identity store users definitions 
security.csrf.enabled=false # Enable CSRF protection
security.csrf.tokenHttpHeaderName=X-CSRF-TOKEN # CSRF token http header name
security.csrf.tokenCookieName=CSRF-TOKEN # CSRF token http cookie name
security.cors.enabled=false # Enable CORS
security.cors.allowOrigin=* # Comma separated list of origins allowed
security.cors.allowHeaders=origin, accept, content-type # Comma separated list of http headers allowed
security.cors.exposeHeaders=location, info # Comma separated list of exposed http headers as part of the response
security.cors.allowCredentials=true # Indicates whether or not cross-site Access-Control requests should be made using credentials such as cookies, authorization headers or TLS client certificates
security.cors.allowMethods=GET, POST, PUT, DELETE, OPTIONS, HEAD, TRACE # List of allowed http methods
security.cors.maxAge=600 # Indicates how long the results of a preflight request can be cached in seconds
```
