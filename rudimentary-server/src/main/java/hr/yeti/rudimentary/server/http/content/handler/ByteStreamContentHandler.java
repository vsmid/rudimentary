package hr.yeti.rudimentary.server.http.content.handler;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.content.ByteStream;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import java.io.IOException;
import java.util.Objects;

public class ByteStreamContentHandler implements ContentHandler<ByteStream> {

    @Override
    public ByteStream read(HttpExchange httpExchange) throws IOException {
        return new ByteStream(httpExchange.getRequestBody());
    }

    @Override
    public void write(int httpStatus, ByteStream data, HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            if (Objects.isNull(data)) {
                httpExchange.sendResponseHeaders(httpStatus, -1);
            } else {
                httpExchange.sendResponseHeaders(httpStatus, 0);
                data.getStreamOutWriteDef().startStreaming(httpExchange.getResponseBody());
                httpExchange.getResponseBody().flush();
                httpExchange.getResponseBody().close();
            }
        }
    }

}
