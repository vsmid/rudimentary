package hr.yeti.rudimentary.server.http.content.handler;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.content.Redirect;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import java.io.IOException;
import java.util.Objects;

public class RedirectContentHandler implements ContentHandler<Redirect> {

    @Override
    public Redirect read(HttpExchange httpExchange) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void write(int httpStatus, Redirect data, HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            httpExchange.sendResponseHeaders(httpStatus, -1);
            if (Objects.nonNull(data)) {
                httpExchange.getResponseHeaders().add("location", data.get().toString());
            }
        }
    }

}
