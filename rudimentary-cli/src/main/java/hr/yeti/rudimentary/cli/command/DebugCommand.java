package hr.yeti.rudimentary.cli.command;

import hr.yeti.rudimentary.cli.Command;
import java.io.IOException;
import java.util.Map;

public class DebugCommand implements Command {

  @Override
  public String name() {
    return "debug";
  }

  @Override
  public String description() {
    return "Run application in debug mode.";
  }

  @Override
  public Map<String, String> options() {
    return Map.of();
  }

  @Override
  public void execute(Map<String, String> arguments) {
    try {
      Process debug = Runtime.getRuntime().exec("sh ./debug.sh");
      debug.waitFor();
    } catch (IOException | InterruptedException ex) {
      ex.printStackTrace();
    }
  }

}
