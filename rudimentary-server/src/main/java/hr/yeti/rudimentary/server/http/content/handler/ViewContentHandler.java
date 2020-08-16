package hr.yeti.rudimentary.server.http.content.handler;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.mvc.spi.ViewEngine;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class ViewContentHandler implements ContentHandler<View> {
    
    private ViewEngine viewEngine;

    @Override
    public View read(HttpExchange httpExchange, Class<? extends HttpEndpoint> httpEndpoint) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void write(int httpStatus, View data, HttpExchange httpExchange, Class<? extends HttpEndpoint> httpEndpoint) throws IOException {
        httpExchange.getResponseHeaders().put("Content-Type", List.of(MediaType.TEXT_HTML));
        if (Objects.isNull(data)) {
            httpExchange.sendResponseHeaders(httpStatus, -1);
        } else {
            byte[] response;
            if (Objects.nonNull(Instance.of(ViewEngine.class))) {
                response = data.get().getBytes(StandardCharsets.UTF_8);
            } else {
                response = "Could not resolve view.".getBytes();
                httpStatus = 500;
            }
            httpExchange.sendResponseHeaders(httpStatus, response.length);
            httpExchange.getResponseBody().write(response);
            httpExchange.getResponseBody().flush();
        }
    }

    @Override
    public Class[] dependsOn() {
        return new Class[]{ ViewEngine.class };
    }
}
