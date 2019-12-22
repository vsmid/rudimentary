# Validation

### Introduction
Rudimentary framework provides simple way to set different types of constraints for incoming http request. These constraints are then checked automatically against `hr.yeti.rudimentary.validation.Validator` on `HttpEndpoint` level. `hr.yeti.rudimentary.validation.Validator` can of course be called manually anywhere in the code if you wish.

## Automatic contraint validation
Automatic contraint validation will happen in the three below stated cases. This means that the validation will be executed by te Rudimentary framework.

### Generic - defining constraints on the `HttpEndpoint` for any request type
You can define constraints for any `HttpEndpoint` by overrding `HttpEndpoint#constraints` method.
```java
...
import static hr.yeti.rudimentary.validation.Constraint.NOT_EMPTY;
import static hr.yeti.rudimentary.validation.Constraint.NOT_NULL;
...

public class TextWithConstraintsEndpoint implements HttpEndpoint<Text, Text> {

    @Override
    public Constraints constraints(
        Text body,
        Map<String, String> pathVariables,
        Map<String, String> queryParameters,
        Headers httpHeaders
    ) {
        return new Constraints() {
            {
                o(body.getValue(), NOT_NULL);
                o(body.getValue(), NOT_EMPTY);
            }
        };
    }

    @Override
    public Text response(Request<Text> request) {
        return new Text("Hello " + request.getBody().getValue());
    }
    
}
```
### POJO - defining constraints on the `Model` level 
If your request body type is a POJO extending `hr.yeti.rudimentary.http.content.Model` class you can define constraints in that POJO. If you also define constraints on the `HttpEndpoint` level then those constraints will be combined.
```java
...
import static hr.yeti.rudimentary.validation.Constraint.NOT_EMPTY;
import static hr.yeti.rudimentary.validation.Constraint.NOT_NULL;
...

public class OkModel extends Model {

    private String name;
    private String description;

    @Override
    public Constraints constraints() {
        return new Constraints() {
            {
                o(name, NOT_NULL);
                o(description, NOT_NULL, NOT_EMPTY);
            }
        };
    }
  
  // Getters, Setters
}
```
```java
public class OkEndpoint implements HttpEndpoint<OkModel, Text> {

    @Override
    public Text response(Request<OkModel> request) {
        return new Text("Hello " + request.getBody().getName());
    }

}
```
### Json - special case of defining constraints for Json http request body
If you wish to work with incoming request body as `Json` but still have a `POJO`constraint validation you could use something like this.
```java
public class JsonOkModelEndpoint implements HttpEndpoint<Json, Text> {

    @Override
    public Constraints constraints(
      Json body, Map<String, String> pathVariables, Map<String, String> queryParameters, Headers httpHeaders) {
        
        // a) Generic - manually define constraints for raw JSON array (or JSON object) if you do not know a type
        /*new Constraints() {
          {
            body.getValue()
                .asJsonArray()
                .forEach(
                    json -> {
                      o(json.asJsonObject().getString("name"), NOT_NULL);
                      o(json.asJsonObject().getString("description"), NOT_NULL);
                    });
          }
        };*/
        
        // b) POJO - if you do know the type, delegate constraint definitions to Model and use it like this
        return new Constraints(body, OkModel.class);
    }

    @Override
    public Text response(Request<Json> request) {
        ...
    }

}
```

## On-demand contraint validation
On-demand contraint validation will happen if you call `hr.yeti.rudimentary.validation.Validator#validate` method anywhere in the code.
```java
String value = "test2";

Validator.validate(new Constraints() {
  {
      o(value, NOT_NULL);
      o(value, NOT_EMPTY);
  }
});
```
## Constraint types
For now Rudimentary framework offers some basic, default constraint types implementation.
At any time you can add your own, custom ones.

* NOT_NULL
* NOT_EMPTY
* MIN
* MAX
* REGEX

#### Custom constraint on-the-fly
```java
String value = "test2";

new Constraints() {
  {
    o(value, NOT_NULL);
    o(value, NOT_EMPTY);
    // Custom constraint on-the-fly
    o(value, (t) -> {
      return new ValidationResult("boohoo".equals(t), Optional.of("Value is not boohoo."));
    });
  }
}
```


