package hr.yeti.rudimentary.cli;

import java.io.IOException;
import java.util.Map;

public interface Command {

  String name();

  String description();

  Map<String, String> options();

  void execute(Map<String, String> arguments);

  default boolean isWindowsOS() {
    return System.getProperty("os.name").toLowerCase().contains("windows");
  }

  default boolean isCygwinOrMingw() {
    boolean cygwinMingw = false;

    Process unameProcess = null;
    try {
      unameProcess = new ProcessBuilder("uname", "-s").start();

      StringBuilder consoleOutput = new StringBuilder();
      int read = 0;

      while ((read = unameProcess.getInputStream().read()) != -1) {
        consoleOutput.append((char) read);
      }
      System.out.println(consoleOutput.toString());

      if (consoleOutput.toString().toLowerCase().contains("mingw") || consoleOutput.toString().toLowerCase().contains("cygwin")) {
        cygwinMingw = true;
      }
    } catch (IOException ex) {
      // Noop.
    }

    return cygwinMingw;
  }
}
