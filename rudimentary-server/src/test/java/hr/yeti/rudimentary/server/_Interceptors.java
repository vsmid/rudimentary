package hr.yeti.rudimentary.server;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.interceptor.spi.AfterInterceptor;
import hr.yeti.rudimentary.interceptor.spi.BeforeInterceptor;

public class _Interceptors {

    public static class BeforeInterceptor1 implements BeforeInterceptor {

        @Override
        public int order() {
            return 1;
        }

        @Override
        public void intercept(Request request) {
            System.out.print("1");
        }

    }

    public static class BeforeInterceptor2 implements BeforeInterceptor {

        @Override
        public int order() {
            return 2;
        }

        @Override
        public void intercept(Request request) {
            System.out.print("2");
        }
    }

    public static class AfterInterceptor1 implements AfterInterceptor {

        @Override
        public int order() {
            return 1;
        }

        @Override
        public void intercept(Request request, Object response) {
            System.out.print("3");
        }

    }

    public static class AfterInterceptor2 implements AfterInterceptor {

        @Override
        public int order() {
            return 2;
        }

        @Override
        public void intercept(Request request, Object response) {
            System.out.print("4");
        }

    }

    public static class BeforeInterceptorForURI implements BeforeInterceptor {

        @Override
        public int order() {
            return 2;
        }

        @Override
        public String applyToURI() {
            return "uriintercept.*before";
        }

        @Override
        public void intercept(Request request) {
            System.out.print("before");
        }

    }

    public static class AfterInterceptorForURI implements AfterInterceptor {

        @Override
        public int order() {
            return 2;
        }

        @Override
        public String applyToURI() {
            return "uriintercept.*after";
        }

        @Override
        public void intercept(Request request, Object response) {
            System.out.print("after");
        }

    }
}
