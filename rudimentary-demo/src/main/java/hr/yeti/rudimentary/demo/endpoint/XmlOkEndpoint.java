package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.demo.model.OkModel;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;

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

    @Override
    public String description() {
        return "Endpoint which receives xml as body and returns xml as response.";
    }

}
