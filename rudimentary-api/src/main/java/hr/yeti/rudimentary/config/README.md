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
  Config.provider().property("name"); // Static access, returns ConfigProperty instance
  Config.provider().property("name", "Lena"); // Static access which also sets default value if no property is found within provider, returns ConfigProperty instance
  Config.provider().value("name"); // Static access, returns String value
    
  new ConfigProperty("name"); // Class level property
  new ConfigProperty("name", "Lena"); // Class level property which also sets default value if no property is found within provider
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

| Group        | Property name        | Default value           |  Description           |
| ------------ | ------------- | ------------- | ----- |
| Server | server.port     | 8888 | Http server port |
| Server | server.threadPool     | 25 | Maximum number of threads that can be processed by server at a time |
| Server | server.stopDelay     | 0 | Maximum time in seconds to wait until requests have finished |
| Server(SSL) | server.ssl.enabled     | false | Enable SSL |
| Server(SSL) | server.ssl.protocol    | TLS | SSL protocol to be used |
| Server(SSL) | server.ssl.keyStore     |  | Key store used for SSL |
| Server(SSL) | server.ssl.keyStore.password    |  | Key store password|
| Server(SSL) | server.ssl.trustStore    | | Trust store used for SSL |
| Server(SSL) | server.ssl.trustStore.password     | | Trust store password |
| Server(SSL) | server.ssl.clientAuth     | false | Should client authentication be required |
| MVC | mvc.templatesDir     | view | Directory where views are located (under src/main/resources) |
| MVC | mvc.staticResourcesDir    | static | Directory where static resources are located (under src/main/resources) |
| JDBC | dataSource.enabled     | false | Enable JDBC data source |
| JDBC | dataSource.driverClassName     |  | JDBC driver class name |
| JDBC | dataSource.jdbcUrl     |  | JDBC url string |
| JDBC | dataSource.username     |  | Database username |
| JDBC | dataSource.password     |  | Database password |
| JDBC | dataSource.maximumPoolSize     | 25 | JDBC connection pool size |
| Session | session.maxConcurrentAllowed     | 25 | Number of concurrent sessions allowed |
| Session | session.inactivityPeriodAllowed     | 1800 | Maximum time allowed between two consequent requests in seconds before session is invalidated |
| Email(pool) | email.smtp.enabled     | false | Enable SMTP |
| Email(pool) | email.pool.minSize     | 25 | Minimim number of javax.mail.Session in the pool |
| Email(pool) | email.pool.maxSize    | 50 | MAximum number of javax.mail.Session in the pool |
| Email(pool) | email.pool.validationInterval     | 30 | Time between two checks of pool status |
| Email(pool) | email.pool.awaitTerminationInterval     | 15 | Time to wait for tasks to finish before termination |
| Email(SMTP) | mail.smtp.host     |  | https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html |
| Email(SMTP) | mail.smtp.port     |  | https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html |
| Email(SMTP) | mail.smtp.user     |  | https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html |
| Email(SMTP) | mail.smtp.password     |  | SMTP user password |
| Email(SMTP) | mail.smtp.auth     |  | https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html |
| Email(SMTP) | mail.smtp.starttls.enable     |  | https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html |
| Email(SMTP) | mail.smtp.starttls.required     |  | https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html |
| Security | security.realm     | default | Realm name |
| Security | security.urisRequiringAuthentication     | | Comma separated list of URIs(regex format allowed) for which authentication will be applied |
| Security | security.urisNotRequiringAuthentication     | | Comma separated list of URIs(regex format allowed) for which authentication will not be applied |
| Security(Basic) | security.basic.enabled    | false | Enable Basic security |
| Security(Login form) | security.loginform.enabled    | false | Enable login form security |
| Security(Login form) | security.loginform.loginURI    | /_login | Login view URI |
| Security(Login form) | security.loginform.redirectAfterSuccessfulLoginURI    | _redirectAfterSuccessfulLogin | Http endpoint which will perform redirect to the landing view |
| Security(Login form) | security.loginform.landingViewURI    |  | Where to land after successful login |
| Security(Login form) | security.loginform.usernameFieldName    | username | Login form username input field name |
| Security(Login form) | security.loginform.passwordFieldName    | password | Login form password input field name |
| Security(Identity store) | security.identityStore.embedded.enabled     | false | Enable in memory identity store. |
| Security(Identity store) | security.identityStore.embedded.identities    |  | In memory identity store users definitions  |
| Security(CSRF) | security.csrf.enabled     | false | Enabel CSRF protection |
| Security(CSRF) | security.csrf.tokenHttpHeaderName     | X-CSRF-TOKEN | CSRF token http header name |
| Security(CSRF) | security.csrf.tokenCookieName     | CSRF-TOKEN | CSRF token http cookie name |
| Security(CORS) | security.cors.enabled    | false | Enable CORS |
| Security(CORS) | security.cors.allowOrigin     | * | Comma separated list of origins allowed |
| Security(CORS) | security.cors.allowHeaders    | origin, accept, content-type | Comma separated list of http headers allowed |
| Security(CORS) | security.cors.exposeHeaders     | location, info | Comma separated list of exposed http headers as part of the response |
| Security(CORS) | security.cors.allowCredentials    | true | Indicates whether or not cross-site Access-Control requests should be made using credentials such as cookies, authorization headers or TLS client certificates |
| Security(CORS) | security.cors.allowMethods     | GET, POST, PUT, DELETE, OPTIONS, HEAD, TRACE | List of allowed http methods |
| Security(CORS) | security.cors.maxAge     | 3600 | Indicates how long the results of a preflight request can be cached. |









