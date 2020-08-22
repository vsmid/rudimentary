package hr.yeti.rudimentary.exts.http.xml;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.content.Pojo;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XmlPojoContentHandler implements ContentHandler<Pojo> {

    @Override
    public Pojo read(HttpExchange httpExchange, Class<? extends HttpEndpoint> httpEndpoint) throws IOException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ContentHandler.getGenericType(httpEndpoint, 0));
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (Pojo) jaxbUnmarshaller.unmarshal(httpExchange.getRequestBody());
        } catch (ClassNotFoundException | JAXBException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void write(int httpStatus, Pojo data, HttpExchange httpExchange, Class<? extends HttpEndpoint> httpEndpoint) throws IOException {
        if (Objects.isNull(data)) {
            httpExchange.sendResponseHeaders(httpStatus, -1);
        } else {
            try {
                StringWriter writer = new StringWriter();
                Marshaller marshaller = JAXBContext.newInstance(data.getClass()).createMarshaller();
                marshaller.marshal(data, writer);

                String response = writer.toString();

                httpExchange.getResponseHeaders().put("Content-Type", List.of(MediaType.APPLICATION_XML));
                httpExchange.sendResponseHeaders(httpStatus, response.getBytes().length);
                httpExchange.getResponseBody().write(response.getBytes());
                httpExchange.getResponseBody().flush();
            } catch (JAXBException ex) {
                throw new IOException(ex);
            }
        }
    }

    @Override
    public boolean activateReader(Class<? extends HttpEndpoint> httpEndpoint, HttpExchange httpExchange) {
        try {
            return ContentHandler.getGenericType(httpEndpoint, 0).getSuperclass().equals(Pojo.class)
                && ((httpExchange.getRequestHeaders().containsKey("Content-Type")
                && httpExchange.getRequestHeaders().get("Content-Type").get(0).equalsIgnoreCase(MediaType.APPLICATION_XML
                )) || (!httpExchange.getRequestHeaders().containsKey("Content-Type")));
        } catch (ClassNotFoundException ex) {
            logger().log(System.Logger.Level.ERROR, ex);
            return false;
        }
    }

    @Override
    public boolean activateWriter(Class<? extends HttpEndpoint> httpEndpoint, HttpExchange httpExchange) {
        try {
            return !ContentHandler.isViewEndpoint(httpEndpoint)
                && ContentHandler.getGenericType(httpEndpoint, 1).getSuperclass().equals(Pojo.class)
                && ((httpExchange.getRequestHeaders().containsKey("Accept")
                && httpExchange.getRequestHeaders().get("Accept").get(0).equalsIgnoreCase(MediaType.APPLICATION_XML
                )) || (!httpExchange.getRequestHeaders().containsKey("Accept")));
        } catch (ClassNotFoundException ex) {
            logger().log(System.Logger.Level.ERROR, ex);
            return false;
        }
    }

}
