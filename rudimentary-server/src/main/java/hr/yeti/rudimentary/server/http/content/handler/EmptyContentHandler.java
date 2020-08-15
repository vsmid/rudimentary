package hr.yeti.rudimentary.server.http.content.handler;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.IOException;
import java.util.List;

public class EmptyContentHandler implements ContentHandler<Empty> {

    @Override
    public Empty read(HttpExchange exchange, Class<? extends HttpEndpoint> httpEndpoint) throws IOException {
        return Empty.INSTANCE;
    }

    @Override
    public void write(int httpStatus, Empty data, HttpExchange httpExchange, Class<? extends HttpEndpoint> httpEndpoint) throws IOException {
        try (httpExchange) {
            httpExchange.getResponseHeaders().put("Content-Type", List.of(MediaType.ALL));
            httpExchange.sendResponseHeaders(httpStatus, -1);
        }
    }

}
