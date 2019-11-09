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

### Configuration provider
Configuration provider is a class which extends `hr.yeti.rudimentary.config.spi.Config` class.
By overriding its `initialize()` method you can define how the properties will be loaded(from the database, file etc.).

Configuration providers should be registered by writing canonical class name of the provider to the `src/main/java/META-INF/services/hr.yeti.rudimentary.config.spi.Config` file.
Fortunately for you, Rudimentary already comes with Maven plugin which does that automatically for you.

At any time, you can create and register your own, custom configuration provider.

 
#### Default configuration provider
By default, Rudimentary provides default configuration provider in the form of `hr.yeti.rudimentary.server.config.DefaultConfigProvider`. This provider loads configuration from the property file located in `src/main/resources/config.properties`.
