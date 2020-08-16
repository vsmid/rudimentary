package hr.yeti.rudimentary.server.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestHttpExchangeImpl extends HttpExchange {

    private InputStream is;
    private OutputStream os;
    private Headers reqHeaders = new Headers();
    private Headers respHeaders = new Headers();
    private int httpStatus;

    public TestHttpExchangeImpl() {
    }

    public TestHttpExchangeImpl(InputStream is) {
        this.is = is;
    }

    @Override
    public Headers getRequestHeaders() {
        return reqHeaders;
    }

    @Override
    public Headers getResponseHeaders() {
        return respHeaders;
    }

    @Override
    public URI getRequestURI() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getRequestMethod() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HttpContext getHttpContext() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() {
        try {
            os.flush();
            os.close();
        } catch (IOException ex) {
            Logger.getLogger(TestHttpExchangeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public InputStream getRequestBody() {
        return is;
    }

    @Override
    public OutputStream getResponseBody() {
        return os;
    }

    @Override
    public void sendResponseHeaders(int rCode, long responseLength) throws IOException {
        this.httpStatus = rCode;
        if (responseLength != -1) {
            this.os = new ByteArrayOutputStream((int) responseLength);
        }
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getResponseCode() {
        return this.httpStatus;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getAttribute(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAttribute(String name, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStreams(InputStream i, OutputStream o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HttpPrincipal getPrincipal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
