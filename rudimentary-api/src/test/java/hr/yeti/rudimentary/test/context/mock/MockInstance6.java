package hr.yeti.rudimentary.test.context.mock;

import hr.yeti.rudimentary.context.spi.Instance;

public class MockInstance6 implements Instance {

    @Override
    public Class[] dependsOn() {
        return new Class[]{ MockInstance7.class };
    }

}
