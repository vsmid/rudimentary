# XML Content Type

This extension allows converting incoming xml to pojo and converting pojo to xml as response.
```java
public class XmlOkEndpoint implements HttpEndpoint<OkModel, OkModel> {

    @Override
    public String path() {
        return "xmlok";
    }

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public OkModel response(Request<OkModel> request) {
        return request.getBody();
    }

}

@XmlRootElement
public class OkModel extends Pojo {

    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
```
