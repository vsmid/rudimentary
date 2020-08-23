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

## Testing HttpEndpoints
There are two ways you can test your http endpoints.

### Testing results of HttpEndpoint instance methods using mocks
This is not real http testing but rather simple way of `HttpEndpoint`provider instance's method unit testing.
```java
// Create mock context with configuration.
// If there is no configuration or other instance dependency you can also use plain old new GetAllCarsEndpoint()
// to create new instance instead of usin Context.
ContextMock ctx = new ContextMock(
    Map.of(), 
    GetAllCarsEndpoint.class
 );
...

Request request = new Request(...); // Yo can use Mockito to create Request mock

GetAllCarsEndpoint endpoint = Instance.of(GetAllCarsEndpoint.class);

Text response = endpoint.response(request));

assertEquals("[]", response.getValue());
```

### Testing aginst TestServer for real http tests
To write real http test you can use `hr.yeti.rudimentary.server.test.TestServer` from `rudimentary-server` module.
This way you configure real http server and you will use http client to send http requests. You can check examples of 
how `TestServer` is used and configured in rudimentary tests [here](https://github.com/vsmid/rudimentary/tree/master/rudimentary-server/src/test/java/hr/yeti/rudimentary/server/http/processor).
```java
public class HttpTest {

    static TestServer testServer;

    @BeforeAll
    public static void beforeAll() {
        // Builder also allows you to configure datasources, authMechanisms, config, 
        // viewEndpoints, httpFilters, interceptors, eventListeners, exceptionHandler 
        // and plain instances
        testServer = TestServer.newBuilder()
            .httpEndpoints(GetAllCarsEndpoint.class) 
            .build();
        testServer.start();
    }

    @AfterAll
    public static void afterAll() {
        testServer.stop();
    }

    @Test
    public void test_http() throws IOException, InterruptedException {
        // setup:
        URI uri = testServer.buildUri("cars"); // To get URI you must use TestServer#buildUri method
        HttpRequest GET = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response;

        when:
        response = HttpClient.newHttpClient().send(GET, HttpResponse.BodyHandlers.ofString());

        then:
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }
```
