## Context

### Introduction
Context is the central point of every Rudimentary application/service. It is the place where all object instances implementing various Rudimentary SPIs are held.

The foundation of the context actually lies within Java's own `java.util.ServiceLoader` utility. Rudimentary context can then be viewed as a simple wrapper around ServiceLoader utility.

### Context provider
Context provider is a class extending `hr.yeti.rudimentary.context.spi.Context` class. This is the only SPI that is not meant to be implemented/overriden by the user of the framework.

#### Default Context provider
Rudimentary provides default Context implementation through `hr.yeti.rudimentary.server.context.DefaultContextProvider`.
This default provider for now only manages application scoped instances which basically means there is only one object instance created per runtime. Currently, this is the only Context provider allowed.

#### Test Context provider
Rudimentary provides `hr.yeti.rudimentary.test.ContextMock` which you can use when writing test cases.
```java
  // Create mock context with configuration
  ContextMock ctx2 = new ContextMock(
      Map.of("key", "value"), 
      Mockinstance1.class, 
      MockInstance2.class
   );
```

## Instance
Everything stored inside Context is Instance. Instance can be considerd as an application scoped singleton.

### Accessing Context object instances
Context instances can be accessed in a static way but that is not the preferred way.
```java
  // Get the whole context
  Map<String, Instance> context = Context.getContext(); 
  
  // Get object instance by key
  MockInstance mockInstance = context.getKey(MockInstance.class.getCanonicalName());
```
The preferred way is by using `hr.yeti.rudimentary.context.spi.Instance`.
```java
  // Get exact instance provider by class
  MockInstance mockInstance = Instance.of(MockInstance.class);
  
  // Get all providers of Instance SPI - these are basically all object instances stored in context.
  // Every Rudimantary SPI should implement `hr.yeti.rudimentary.context.spi.Instance`. 
  List<Instance> instances = Instance.of(Instance.class);
  
  // Get only providers of specific SPI
  List<HttpEndpoint> instances = Instance.of(HttpEndpoint.class);
  
  // Get dataSource provider of BasicDataSource SPI by Instance#id
  DefaultDataSource ds = Instance.withId(BasicDataSource.class, "myDataSourceId");
  
```
### Registering custom Instance provider with Rudimentary context
Simply put, if you want Rudimentray context to automatically initialize custom instance for you, just make your class implement 
`hr.yeti.rudimentary.context.spi.Instance`. That's it.

*rudimentary-maven-plugin* will automatically register new instance but you can also register it manually by writing canonical class name of the provider to the `src/main/resources/META-INF/services/hr.yeti.rudimentary.context.spi.Instance` file.

#### Instance lifecycle and phases
#### Initialization
Every instance must be initialized. Context triggers each instance initialization method before caching it internally.
Your custom object initialization logic should be placed inside overriden method called `initialize()`.

```java
public class A implements Instance {

  @Override
  public void initialize() {
    // TODO Put your logic here.
  }
}
```

#### Destroy
Your custom object destroy logic should be placed inside overriden method called `destroy()`.
It will be called when context is destroyed(server stop).


```java
public class A implements Instance {

  @Override
  public void destroy() {
    // TODO Put your logic here.
  }
}
```

#### Conditional
Logic for conditional object initialization should be placed inside overriden method called `conditional()`.

```java
public class A implements Instance {

  @Override
  public void conditional() {
    return 1 < 2; // Will initialize instance if condition is true.
  }
}
```

#### DependsOn
If your custom object instance dependes on some other instance to be loaded before override method `dependsOn()`.

```java
public class A implements Instance {

  @Override
  public Class[] dependsOn() {
    return new Class[]{ Config.class }; // Config instance must be initialized before.
  }
}
```

#### Id
You can give an instance a custom id in case you have multiple providers of the same SPI. 
This is helpful if you are not fetching instance by the exact class but by SPI class.
Example is how Rudimentary handle multiple `hr.yeti.rudimentary.sql.spi.BasicDataSource` providers.


#### Primary
In case you have multiple providers of the same SPI and you want to return the specific one, set primary to true;
This is helpful if you are not fetching instance by the exact class but by SPI class.

```java
// This instance will be returned when called Instance.of(Instance.class)
public class A implements Instance {

  @Override
  public boolean primary() {
    return true;
}

public class B implements Instance {

  @Override
  public boolean primary() {
    return false;
}
```
