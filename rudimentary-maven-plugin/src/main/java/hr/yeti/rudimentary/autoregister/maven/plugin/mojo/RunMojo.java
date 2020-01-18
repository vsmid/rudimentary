package hr.yeti.rudimentary.autoregister.maven.plugin.mojo;

import hr.yeti.rudimentary.autoregister.maven.plugin.Command;
import hr.yeti.rudimentary.autoregister.maven.plugin.ConsoleActionListener;
import hr.yeti.rudimentary.autoregister.maven.plugin.ConsoleReader;
import hr.yeti.rudimentary.autoregister.maven.plugin.Watcher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Runs project.
 *
 * @author vedransmid@yeti-it.hr
 */
@Mojo(name = "run")
public class RunMojo extends AbstractMojo implements Command {

    private final Pattern pattern = Pattern.compile(".*mainClass\\>(.*)\\</mainClass.*");

    /**
     * Run in debug mode.
     */
    @Parameter(property = "debug", defaultValue = "true")
    String debug;

    /**
     * Set debug port.
     */
    @Parameter(property = "debugPort", defaultValue = "1044")
    String debugPort;

    /**
     * Set system properties.
     */
    @Parameter(property = "props", defaultValue = "")
    String props;

    /**
     * Reload on changes. Active by default.
     */
    @Parameter(property = "reload", defaultValue = "true")
    String reload;

    public String debugSettings = "";
    public String systemProperties;
    public String mainClass;

    public Process process;
    public ConsoleReader consoleReader;
    public String pid;
    public Watcher watcher;

    public static final String ENTER = "";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {

            if (Boolean.valueOf(debug)) {
                debugSettings = " -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=" + debugPort + " ";
            }

            systemProperties = Objects.isNull(props) ? "" : props;
            mainClass = parsePOMForMainClass();

            System.out.println("[Iteration #0]");

            if (mavenCompileProject()) {
                mavenRunRudyApplication();
            }

            registerTestRunner();
            readProcessStdOut();

            if (Boolean.valueOf(reload)) {
                watcher = new Watcher(new File("").toPath(), true, this);
                watcher.processEvents();
            }

        } catch (IOException | RuntimeException | InterruptedException ex) {
            System.err.println(ex.getMessage());
            // Noop.
        }
    }

    public boolean mavenCompileProject() throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(mvn(), "compile", "package", "-DskipTests", "-DskipITs");
        builder.redirectErrorStream(true);

        File output = File.createTempFile("output", "rudi");
        builder.redirectOutput(output);

        File input = File.createTempFile("input", "rudi");
        builder.redirectInput(input);

        Process compile = builder.start();
        compile.waitFor();

        boolean success = compile.exitValue() == 0;

        if (!success) {
            String error = new String(Files.readAllBytes(output.toPath()))
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));

            error
                .substring(
                    error.indexOf("[ERROR] COMPILATION ERROR : "),
                    error.indexOf("[INFO] BUILD FAILURE")
                )
                .lines()
                .filter(line -> line.startsWith("[ERROR]"))
                .forEach(System.out::println);

        }

        return success;
    }

    public void mavenRunRudyApplication() throws IOException {
        ProcessBuilder builder = new ProcessBuilder(mvn(),
            "\"-Dexec.args=" + systemProperties + " -classpath %classpath " + debugSettings + mainClass + "\"",
            "-Dexec.executable=java", "-Dexec.classpathScope=runtime", "compile", "package", "exec:exec");

        builder.redirectErrorStream(true);
        process = builder.start();
        System.out.println("Debugger is listening on port " + debugPort);
    }

    public void readProcessStdOut() {
        this.consoleReader = new ConsoleReader(this);
        new Thread(this.consoleReader).start();
    }

    public void registerTestRunner() {
        new Thread(new ConsoleActionListener(ENTER, (input) -> {
            try {
                ProcessBuilder testBuilder = new ProcessBuilder(mvn(),
                    "test-compile", "surefire:test");
                Process test = testBuilder.inheritIO().start();
                test.waitFor();

                ProcessBuilder iTestBuilder = new ProcessBuilder(mvn(),
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

        if (Objects.isNull(mvn)) {
            mvn = System.getenv("MAVEN_HOME");
            if (Objects.nonNull(mvn)) {
                mvn = System.getProperty("maven.home");
            }
        }
        if (Objects.isNull(mvn)) {
            throw new RuntimeException("Is Maven installed on your machine? Try setting M2_HOME env. variable.");
        }

        return mvn + File.separator + "bin" + File.separator + "mvn" + (isWindowsOS() ? ".cmd" : "");
    }

}
