package hr.yeti.rudimentary.server.http.content.handler;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.HttpRequestUtils;
import hr.yeti.rudimentary.http.content.Form;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import java.io.IOException;

public class FormContentHandler implements ContentHandler<Form> {

    @Override
    public Form read(HttpExchange httpExchange) throws IOException {
        byte[] data = httpExchange.getRequestBody().readAllBytes();
        return new Form(
            HttpRequestUtils.parseQueryParameters(new String(data))
        );
    }

    @Override
    public void write(int httpStatus, Form data, HttpExchange httpExchange) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
