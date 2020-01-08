package hr.yeti.rudimentary.server;

import hr.yeti.rudimentary.context.spi.Instance;

public class _Instances {

    public static class SimpleInstance implements Instance {

        @Override
        public void initialize() {
            System.out.print("init.");
        }

    }
}
