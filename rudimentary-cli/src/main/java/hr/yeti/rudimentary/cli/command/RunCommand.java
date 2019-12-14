package hr.yeti.rudimentary.cli.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hr.yeti.rudimentary.cli.Command;
import hr.yeti.rudimentary.cli.ConsoleActionListener;
import hr.yeti.rudimentary.cli.ConsoleReader;
import hr.yeti.rudimentary.cli.Watcher;

public class RunCommand implements Command {

    private final Pattern pattern = Pattern.compile(".*mainClass\\>(.*)\\</mainClass.*");

    public String debugSettings = "";
    public String systemProperties;
    public String mainClass;

    public Process process;
    public ConsoleReader consoleReader;
    public String pid;
    public Watcher watcher;

    public static final String ENTER = "";

    @Override
    public String name() {
        return "run";
    }

    @Override
    public String shorthand() {
        return "r";
    }

    @Override
    public String description() {
        return "Run application.";
    }

    @Override
    public Map<String, String> options() {
        return Map.of(
            "debug", "Run in debug mode.",
            "debugPort", "Set debug mode listening port. Defauls to 1044.",
            "props", "Set system properties. For multiple values enclose in double quotes.",
            "reload", "Reload application on change. Active by default. Set to false to deactivate.");
    }

    @Override
    public void execute(Map<String, String> arguments) {
        try {

            if (arguments.containsKey("debug")) {
                String port = arguments.getOrDefault("debugPort", "1044");
                debugSettings = " -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=" + port + " ";
            }

            systemProperties = arguments.getOrDefault("props", "");
            mainClass = parsePOMForMainClass();

            mavenRunRudyApplication();
            registerTestRunner();
            readProcessStdOut();

            boolean reload = Boolean.valueOf(arguments.getOrDefault("reload", "true"));

            if (reload) {
                watcher = new Watcher(new File("").toPath(), true, this);
                watcher.processEvents();
            }

        } catch (IOException | RuntimeException ex) {
            System.err.println(ex.getMessage());
            // Noop.
        }
    }

    public void mavenRunRudyApplication() throws IOException {
        System.out.println("...");

        ProcessBuilder builder = new ProcessBuilder(mvn() + "/bin/mvn" + (isWindowsOS() ? ".cmd" : ""),
            "\"-Dexec.args=" + systemProperties + " -classpath %classpath " + debugSettings + mainClass + "\"",
            "-Dexec.executable=java", "-Dexec.classpathScope=runtime", "compile", "package", "exec:exec");

        builder.redirectErrorStream(true);
        process = builder.start();
    }

    public void readProcessStdOut() {
        this.consoleReader = new ConsoleReader(this);
        new Thread(this.consoleReader).start();
    }

    public void registerTestRunner() {
        new Thread(new ConsoleActionListener(ENTER, (input) -> {
            try {
                ProcessBuilder testBuilder = new ProcessBuilder(mvn() + "/bin/mvn" + (isWindowsOS() ? ".cmd" : ""),
                    "test-compile", "surefire:test");
                Process test = testBuilder.inheritIO().start();
                test.waitFor();

                ProcessBuilder iTestBuilder = new ProcessBuilder(mvn() + "/bin/mvn" + (isWindowsOS() ? ".cmd" : ""),
                    "failsafe:integration-test");
                Process iTest = iTestBuilder.inheritIO().start();
                iTest.waitFor();
            } catch (IOException | InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
        })).start();
    }

    private String parsePOMForMainClass() {
        try ( FileInputStream pom = new FileInputStream("pom.xml")) {
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

        throw new RuntimeException("Is Maven installed on your machine? Try setting M2_HOME env. variable.");
    }

}
