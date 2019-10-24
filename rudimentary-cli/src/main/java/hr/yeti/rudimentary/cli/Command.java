package hr.yeti.rudimentary.cli;

import java.util.Map;

public interface Command {

  String name();

  String shorthand();

  String description();

  Map<String, String> options();

  void execute(Map<String, String> arguments);

  default boolean isWindowsOS() {
    return System.getProperty("os.name").toLowerCase().contains("windows");
  }

}
