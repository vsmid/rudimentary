package hr.yeti.rudimentary.test.context.mock;

import hr.yeti.rudimentary.context.spi.Instance;

public class MockInstance5 implements Instance {

    @Override
    public Class[] dependsOn() {
        return new Class[]{ MockInstance2.class };
    }

}
