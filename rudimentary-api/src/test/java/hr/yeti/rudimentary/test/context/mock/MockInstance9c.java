package hr.yeti.rudimentary.test.context.mock;

import hr.yeti.rudimentary.context.spi.Instance;

public class MockInstance9c implements Instance {

    @Override
    public boolean primary() {
        return true;
    }

    @Override
    public Class[] dependsOn() {
        return new Class[]{ MockInstance9a.class };
    }

}
