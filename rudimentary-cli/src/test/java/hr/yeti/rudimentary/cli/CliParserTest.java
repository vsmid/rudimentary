package hr.yeti.rudimentary.cli;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class CliParserTest {

    @Test
    public void test_arg_with_no_value_is_put_to_options_map_with_null_value() {
        CliParser parser;

        when:
        parser = new CliParser(new String[]{ "run", "--debug" });

        then:
        assertTrue(parser.options().containsKey("debug"));
        assertNull(parser.options().get("debug"));
    }

}
