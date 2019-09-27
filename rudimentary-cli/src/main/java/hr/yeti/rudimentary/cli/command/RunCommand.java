package hr.yeti.rudimentary.cli.command;

import hr.yeti.rudimentary.cli.Command;
import hr.yeti.rudimentary.cli.ConsoleReader;
import hr.yeti.rudimentary.cli.Watcher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunCommand implements Command {

  private final Pattern pattern = Pattern.compile(".*mainClass\\>(.*)\\</mainClass.*");

  public String debugSettings = "";
  public String systemProperties;
  public String mainClass;

  public Process process;
  public ConsoleReader consoleReader;
  public String pid;
  public Watcher watcher;

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
        "props", "Set system properties. For multiple values enclose in double quotes.",
        "reload", "Reload application on change."
    );
  }

  @Override
  public void execute(Map<String, String> arguments) {
    try {

      if (arguments.containsKey("debug")) {
        String port = arguments.getOrDefault("port", "1044");
        debugSettings = " -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=" + port + " ";
      }

      systemProperties = arguments.getOrDefault("props", "");
      mainClass = parsePOMForMainClass();

      mavenRunRudyApplication();
      readProcessStdOut();

      if (arguments.containsKey("reload")) {
        watcher = new Watcher(new File("").toPath(), true, this);
        watcher.processEvents();
      }

    } catch (IOException ex) {
      ex.printStackTrace();
      // Noop.
    }
  }

  public void mavenRunRudyApplication() throws IOException {
    ProcessBuilder builder = new ProcessBuilder(
        mvn() + "/bin/mvn" + (isWindowsOS() || (isWindowsOS() && isCygwinOrMingw()) ? ".cmd" : ""),
        "\"-Dexec.args=" + systemProperties + " -classpath %classpath " + debugSettings + mainClass + "\"",
        "-Dexec.executable=java",
        "-Dexec.classpathScope=runtime",
        "compile",
        "package",
        "exec:exec"
    );

    builder.redirectErrorStream(true);
    process = builder.start();
  }

  public void readProcessStdOut() {
    this.consoleReader = new ConsoleReader(this);
    new Thread(this.consoleReader).start();
  }

  private String parsePOMForMainClass() {
    try (FileInputStream pom = new FileInputStream("pom.xml")) {
      String pomContent = new String(pom.readAllBytes(), StandardCharsets.UTF_8);

      Matcher matcher = pattern.matcher(pomContent);

      if (matcher.find()) {
        return matcher.group(1);
      }
    } catch (IOException ex) {
      System.err.println("Could not detect main class.");
    }
    return null;
  }

  private String mvn() {
    String mvn = System.getenv("M2_HOME");

    if (Objects.nonNull(mvn)) {
      return mvn;
    }

    mvn = System.getenv("MAVEN_HOME");

    if (Objects.nonNull(mvn)) {
      return mvn;
    }

    mvn = System.getProperty("maven.home");

    if (Objects.nonNull(mvn)) {
      return mvn;
    }

    throw new RuntimeException("Is Maven installed on your machine?");
  }

}
