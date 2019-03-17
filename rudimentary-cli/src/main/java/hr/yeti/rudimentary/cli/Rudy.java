package hr.yeti.rudimentary.cli;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ServiceLoader;

public class Rudy {

  public static final Map<String, Command> COMMANDS = new HashMap<>();
  public static final Properties CLI_INFO = new Properties();

  public static void main(String[] args) throws IOException {
    CLI_INFO.load(Rudy.class.getClassLoader().getResourceAsStream("META-INF/maven/hr.yeti.rudimentary/rudimentary-cli/pom.properties"));

    ServiceLoader.load(Command.class).forEach(command -> COMMANDS.put(command.name(), command));

    CliParser cliParser = new CliParser(args);

    Command command = COMMANDS.get(cliParser.command());

    if (Objects.isNull(command)) {
      System.console().printf("Hmm, no such command[%s], type help for help." + System.lineSeparator(), cliParser.command());
    } else {
      command.execute(cliParser.options());
    }
  }

}
