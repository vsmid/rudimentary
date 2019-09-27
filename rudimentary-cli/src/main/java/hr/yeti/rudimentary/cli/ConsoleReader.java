package hr.yeti.rudimentary.cli;

import hr.yeti.rudimentary.cli.command.RunCommand;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class ConsoleReader implements Runnable {

  private volatile boolean stop = false;

  public RunCommand cmd;

  public ConsoleReader(RunCommand cmd) {
    this.cmd = cmd;
  }

  @Override
  public void run() {
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(cmd.process.getInputStream()));
      String line;
      while ((line = in.readLine()) != null && !stop) {
        if (Objects.isNull(cmd.pid)) {
          if (line.contains("[Java PID=") && line.endsWith("]...")) {
            cmd.pid = line.substring(line.indexOf("=") + 1, line.indexOf("]"));
            System.out.println(line);
          }
        } else {
          System.out.println(line);
        }
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  public void setStop(boolean stop) {
    this.stop = stop;
  }

}
