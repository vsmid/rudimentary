package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Form;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;

public class FormEndpoint implements HttpEndpoint<Form, Text> {

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "/form";
    }

    @Override
    public Text response(Request<Form> request) {
        return new Text(request.getBody().getValue().toString());
    }

}
