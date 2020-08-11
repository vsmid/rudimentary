package hr.yeti.rudimentary.server;

import hr.yeti.rudimentary.exception.ExceptionInfo;
import hr.yeti.rudimentary.exception.spi.ExceptionHandler;

public class _ExceptionHandlers {

    public static class MyGlobalExceptionHandler implements ExceptionHandler {

        @Override
        public ExceptionInfo onException(Exception e) {
            return new ExceptionInfo(999, e.getMessage().getBytes());
        }

    }
}
