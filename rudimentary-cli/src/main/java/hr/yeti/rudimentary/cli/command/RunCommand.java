package hr.yeti.rudimentary.cli.command;

import hr.yeti.rudimentary.cli.Command;
import java.io.IOException;
import java.util.Map;

public class RunCommand implements Command {

  @Override
  public String name() {
    return "run";
  }

  @Override
  public String description() {
    return "Run application.";
  }

  @Override
  public Map<String, String> options() {
    return Map.of("debug", "Run in debug mode. Default value is set to false.");
  }

  @Override
  public void execute(Map<String, String> arguments) {
    try {
      String script = "run";

      if (arguments.containsKey("debug")) {
        script = "debug";
      }

      Process run = Runtime.getRuntime().exec("sh ./" + script + ".sh");

      run.waitFor();

    } catch (IOException | InterruptedException ex) {
      // Noop.
    }
  }

}
