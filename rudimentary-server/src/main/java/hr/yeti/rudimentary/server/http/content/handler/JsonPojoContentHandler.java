package hr.yeti.rudimentary.server.http.content.handler;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.HttpEndpointUtils;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.content.Pojo;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.bind.JsonbBuilder;

public class JsonPojoContentHandler implements ContentHandler<Pojo> {

    @Override
    public Pojo read(HttpExchange httpExchange, Class<HttpEndpoint> httpEndpoint) throws IOException {
        try {
            return (Pojo) JsonbBuilder.create().fromJson(
                httpExchange.getRequestBody(),
                HttpEndpointUtils.getRequestBodyType(httpEndpoint)
            );
        } catch (ClassNotFoundException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void write(int httpStatus, Pojo data, HttpExchange httpExchange, Class<HttpEndpoint> httpEndpoint) throws IOException {
        try (httpExchange) {
            httpExchange.getResponseHeaders().put("Content-Type", List.of(MediaType.APPLICATION_JSON));
            if (Objects.isNull(data)) {
                httpExchange.sendResponseHeaders(httpStatus, -1);
            } else {
                byte[] response = JsonbBuilder.create().toJson((data)).getBytes(StandardCharsets.UTF_8);
                httpExchange.sendResponseHeaders(httpStatus, response.length);
                httpExchange.getResponseBody().write(response);
                httpExchange.getResponseBody().flush();
            }
        }
    }

    @Override
    public boolean activateReader(Class<HttpEndpoint> httpEndpoint, Class<Pojo> model, HttpExchange httpExchange) {
        try {
            return HttpEndpointUtils.getRequestBodyType(httpEndpoint).getSuperclass().equals(Pojo.class)
                && ((httpExchange.getRequestHeaders().containsKey("Content-Type")
                && httpExchange.getRequestHeaders().get("Content-Type").get(0).equalsIgnoreCase(MediaType.APPLICATION_JSON
                )) || (!httpExchange.getRequestHeaders().containsKey("Content-Type")));
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ContentHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean activateWriter(Class<HttpEndpoint> httpEndpoint, Class<Pojo> model, HttpExchange httpExchange) {
        try {
            return HttpEndpointUtils.getResponseBodyType(httpEndpoint).getSuperclass().equals(Pojo.class)
                && ((httpExchange.getRequestHeaders().containsKey("Accept")
                && httpExchange.getRequestHeaders().get("Accept").get(0).equalsIgnoreCase(MediaType.APPLICATION_JSON
                )) || (!httpExchange.getRequestHeaders().containsKey("Accept")));
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ContentHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

}
