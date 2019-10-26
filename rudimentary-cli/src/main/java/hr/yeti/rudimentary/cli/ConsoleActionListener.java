package hr.yeti.rudimentary.cli;

import java.util.Scanner;
import java.util.function.Consumer;

public class ConsoleActionListener implements Runnable {

  private String reactOn;
  private Consumer<String> action;

  public ConsoleActionListener(String reactOn, Consumer<String> action) {
    this.reactOn = reactOn;
    this.action = action;
  }

  @Override
  public void run() {
    Scanner in = new Scanner(System.in);

    while (true) {
      String entry = in.nextLine();

      if (entry.isBlank()) {
        action.accept(entry);
      }
    }

  }

}
