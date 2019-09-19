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
    return Map.of();
  }

  @Override
  public void execute(Map<String, String> arguments) {
    try {
      Process run = Runtime.getRuntime().exec("sh ./run.sh");
      run.waitFor();
    } catch (IOException | InterruptedException ex) {
      ex.printStackTrace();
    }
  }

}
