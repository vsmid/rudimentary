package hr.yeti.rudimentary.cli;

import java.util.Map;

public interface Command {

  String name();

  String description();

  Map<String, String> options();

  void execute(Map<String, String> arguments);
}
