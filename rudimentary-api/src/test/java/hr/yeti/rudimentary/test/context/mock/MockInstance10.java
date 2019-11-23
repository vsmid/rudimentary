package hr.yeti.rudimentary.test.context.mock;

import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Instance;

public class MockInstance10 implements Instance {

    String val;

    @Override
    public void initialize() {
        val = Config.provider().property("val").value();
    }

    @Override
    public boolean primary() {
        return false;
    }

    public String getVal() {
        return val;
    }

}
