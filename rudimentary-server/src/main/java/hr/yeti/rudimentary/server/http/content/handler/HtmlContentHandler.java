package hr.yeti.rudimentary.server.http.content.handler;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.content.Html;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class HtmlContentHandler implements ContentHandler<Html> {

    @Override
    public Html read(HttpExchange httpExchange, Class<? extends HttpEndpoint> httpEndpoint) throws IOException {
        byte[] data = httpExchange.getRequestBody().readAllBytes();
        return new Html(new String(data));
    }

    @Override
    public void write(int httpStatus, Html data, HttpExchange httpExchange, Class<? extends HttpEndpoint> httpEndpoint) throws IOException {
        httpExchange.getResponseHeaders().put("Content-Type", List.of(MediaType.TEXT_HTML));
        if (Objects.isNull(data)) {
            httpExchange.sendResponseHeaders(httpStatus, -1);
        } else {
            byte[] response = data.get().getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(httpStatus, response.length);
            httpExchange.getResponseBody().write(response);
            httpExchange.getResponseBody().flush();
        }
    }

}
