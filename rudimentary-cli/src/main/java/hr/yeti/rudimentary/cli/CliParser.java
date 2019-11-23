package hr.yeti.rudimentary.cli;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CliParser {

    private static final String OPTION_PREFIX = "--";
    private static final int COMMAND_ARG_INDEX = 0;

    private String[] args;

    // TODO Use regex to check command/options format
    public CliParser(String[] args) {
        this.args = args;
    }

    public String command() {
        if (!empty()) {
            return args[COMMAND_ARG_INDEX];
        }

        return "";
    }

    public Map<String, String> options() {
        if (empty()) {
            return Map.of();
        }

        Map<String, String> options = new HashMap<>();
        String activeOption = null;

        for (int index = COMMAND_ARG_INDEX + 1; index < args.length; index++) {
            if (isOption(args[index])) {
                if (Objects.nonNull(activeOption)) {
                    options.put(activeOption, null);
                }
                activeOption = args[index].substring(2);
            } else {
                options.put(activeOption, args[index]);
                activeOption = null;
            }
        }

        if (Objects.nonNull(activeOption)) {
            options.put(activeOption, null);
        }

        return options;
    }

    public boolean empty() {
        return Objects.isNull(this.args) || this.args.length == 0;
    }

    private boolean isOption(String value) {
        return value.startsWith(OPTION_PREFIX);
    }

}
