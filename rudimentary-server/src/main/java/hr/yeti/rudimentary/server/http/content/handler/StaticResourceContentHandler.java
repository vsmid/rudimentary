package hr.yeti.rudimentary.server.http.content.handler;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.content.StaticResource;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class StaticResourceContentHandler implements ContentHandler<StaticResource> {

    @Override
    public StaticResource read(HttpExchange httpExchange, Class<HttpEndpoint> httpEndpoint) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void write(int httpStatus, StaticResource data, HttpExchange httpExchange, Class<HttpEndpoint> httpEndpoint) throws IOException {
        try (httpExchange) {
            if (Objects.isNull(data)) {
                httpExchange.getResponseHeaders().put("Content-Type", List.of(MediaType.ALL));
                httpExchange.sendResponseHeaders(httpStatus, -1);
            } else {
                httpExchange.getResponseHeaders().put("Content-Type", List.of(data.getMediaType()));
                try ( InputStream is = data.get()) {
                    byte[] response = is.readAllBytes();
                    httpExchange.sendResponseHeaders(httpStatus, response.length);
                    httpExchange.getResponseBody().write(response);
                    httpExchange.getResponseBody().flush();
                }
            }
        }
    }
}
