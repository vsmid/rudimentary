package hr.yeti.rudimentary.server.http.content.handler;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.content.Json;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import javax.json.JsonValue;
import javax.json.bind.JsonbBuilder;

public class JsonContentHandler implements ContentHandler<Json> {

    @Override
    public Json read(HttpExchange httpExchange, Class<HttpEndpoint> httpEndpoint) throws IOException {
        JsonValue data = JsonbBuilder.create().fromJson(httpExchange.getRequestBody(), JsonValue.class);
        return new Json(data);
    }

    @Override
    public void write(int httpStatus, Json data, HttpExchange httpExchange, Class<HttpEndpoint> httpEndpoint) throws IOException {
        try (httpExchange) {
            httpExchange.getResponseHeaders().put("Content-Type", List.of(MediaType.APPLICATION_JSON));
            if (Objects.isNull(data)) {
                httpExchange.sendResponseHeaders(httpStatus, -1);
            } else {
                byte[] response = data.get().toString().getBytes(StandardCharsets.UTF_8);
                httpExchange.sendResponseHeaders(httpStatus, response.length);
                httpExchange.getResponseBody().write(response);
                httpExchange.getResponseBody().flush();
            }
        }
    }

}
