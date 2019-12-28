# Testing

## Introduction
Writing test should not be hard. Test execution should be fast. Rudimentary offers only a small helper class which helps you in creating Rudimentary context used in test cases. Everything else is plain [JUnit5](https://junit.org/junit5/).

## Test Context provider
Rudimentary provides `hr.yeti.rudimentary.test.ContextMock` which you can use when writing test cases to reduce bolerplate code used for instance initialization.
```java
// Create mock context with configuration
ContextMock ctx = new ContextMock(
    Map.of("key", "value"), 
    Mockinstance1.class, 
    MockInstance2.class
 );
```
After this, you can get instance via `Instance#of` method.

## Why no test server to test http endpoints with http client?
This will maybe be added in the future but for now you are perfectly fine with testing method results of your `HttpEndpoint` instances. It is much faster and simpler.
