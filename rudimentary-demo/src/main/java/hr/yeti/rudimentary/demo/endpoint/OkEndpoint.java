package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.demo.model.OkModel;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.content.Text;
import static hr.yeti.rudimentary.validation.Constraint.NOT_NULL;
import hr.yeti.rudimentary.validation.Constraints;
import java.lang.System.Logger.Level;

public class OkEndpoint implements HttpEndpoint<OkModel, Text> {

    ConfigProperty server = new ConfigProperty("server.port", "8888");

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "/ok/:id";
    }

    @Override
    public Constraints constraints(Request<OkModel> request) {
        return new Constraints() {
            {
                o(request.queryParam("name"), NOT_NULL);
            }
        };
    }

    @Override
    public int httpStatus() {
        return 201;
    }

    @Override
    public Text response(Request<OkModel> request) {
        logger().log(Level.INFO, Thread.currentThread().getName() + ":: PV ::" + request.pathVar("id") + ":: QP ::" + request.queryParam("name"));

        return new Text("Hello " + request.getBody().getName());
    }

}
