package hr.yeti.rudimentary.test.context.mock;

import hr.yeti.rudimentary.context.spi.Instance;

public class MockInstance9b implements Instance {

    @Override
    public boolean primary() {
        return true;
    }

    @Override
    public Class[] dependsOn() {
        return new Class[]{ MockInstance9c.class };
    }
}
