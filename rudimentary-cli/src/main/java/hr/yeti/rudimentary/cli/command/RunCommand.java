package hr.yeti.rudimentary.cli.command;

import hr.yeti.rudimentary.cli.Command;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    return Map.of(
        "debug", "Run in debug mode.",
        "port", "Set debug mode listening port. Defauls to 1044.",
        "props", "Set system properties. For multiple values enclose in double quotes."
    );
  }

  @Override
  public void execute(Map<String, String> arguments) {
    try {
      String debugSettings = "";
      String systemProperties;

      if (arguments.containsKey("debug")) {
        String port = arguments.getOrDefault("port", "1044");
        debugSettings = " -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=" + port + " ";
      }

      systemProperties = arguments.getOrDefault("props", "");

      String mainClass = parsePOMForMainClass();

      ProcessBuilder builder = new ProcessBuilder(
          isWindowsOS() && !isCygwinOrMingw() ? "mvn.cmd" : "mvn",
          "\"-Dexec.args=" + systemProperties + " -classpath %classpath " + debugSettings + mainClass + "\"",
          "-Dexec.executable=java",
          "-Dexec.classpathScope=runtime",
          "compile",
          "package",
          "exec:exec"
      );

      builder.redirectErrorStream(true);
      Process process = builder.start();

      String pid = null;

      BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;

      while ((line = in.readLine()) != null) {
        if (Objects.isNull(pid)) {
          if (line.contains("[Java PID=") && line.endsWith("]...")) {
            pid = line.substring(line.indexOf("=") - 1, line.indexOf("]"));
            System.out.println(line);
          }
        } else {
          System.out.println(line);
        }
      }

      process.waitFor();

    } catch (IOException | InterruptedException ex) {
      ex.printStackTrace();
      // Noop.
    }
  }

  private String parsePOMForMainClass() {
    try (FileInputStream pom = new FileInputStream("pom.xml")) {
      String pomContent = new String(pom.readAllBytes(), StandardCharsets.UTF_8);

      Pattern pattern = Pattern.compile(".*mainClass\\>(.*)\\</mainClass.*");

      Matcher matcher = pattern.matcher(pomContent);

      if (matcher.find()) {
        return matcher.group(1);
      }
    } catch (IOException ex) {
      System.err.println("Could not detect main class.");
    }
    return null;
  }

}
