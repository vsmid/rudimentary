package hr.yeti.rudimentary.server.http.content.handler;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.content.Redirect;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.IOException;
import java.util.Objects;

public class RedirectContentHandler implements ContentHandler<Redirect> {

    @Override
    public Redirect read(HttpExchange httpExchange, Class<? extends HttpEndpoint> httpEndpoint) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void write(int httpStatus, Redirect data, HttpExchange httpExchange, Class<? extends HttpEndpoint> httpEndpoint) throws IOException {
        if (Objects.isNull(data)) {
            httpExchange.sendResponseHeaders(500, -1);
        } else {
            httpExchange.getResponseHeaders().add("Location", data.get().toString());
            httpExchange.sendResponseHeaders(data.getHttpStatus(), -1);
        }
    }

}
