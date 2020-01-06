package hr.yeti.rudimentary.server;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.http.filter.spi.HttpFilter;
import java.io.IOException;

public class _HttpFilters {

    public static class HttpFilter1 extends HttpFilter {

        @Override
        public int order() {
            return 1;
        }

        @Override
        public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
            System.out.print("1");
        }

        @Override
        public String description() {
            return "";
        }

    }
    
    public static class HttpFilter2 extends HttpFilter {

        @Override
        public int order() {
            return 2;
        }

        @Override
        public void doFilter(HttpExchange exchange, Filter.Chain chain) throws IOException {
            System.out.print("2");
        }

        @Override
        public String description() {
            return "";
        }

    }

}
