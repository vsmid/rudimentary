package hr.yeti.rudimentary.demo;

import hr.yeti.rudimentary.shutdown.spi.ShutdownHook;

public class OnShutdown implements ShutdownHook {

    @Override
    public void onShutdown() {
        System.out.println("Executing shutdown hook operation.");
    }

}
