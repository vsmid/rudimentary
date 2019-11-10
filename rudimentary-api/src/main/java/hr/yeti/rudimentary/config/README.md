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

