package hr.yeti.rudimentary.server;

import com.sun.net.httpserver.Headers;
import hr.yeti.rudimentary.exception.ExceptionInfo;
import hr.yeti.rudimentary.exception.spi.ExceptionHandler;

public class _ExceptionHandlers {

    public static class MyGlobalExceptionHandler implements ExceptionHandler {

        @Override
        public ExceptionInfo onException(Exception e, Headers responseHttpHeaders) {
            responseHttpHeaders.add("Custom-Header", "Lena");
            return new ExceptionInfo(999, e.getMessage().getBytes());
        }

    }
}
