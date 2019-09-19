package hr.yeti.rudimentary.cli.command;

import hr.yeti.rudimentary.cli.Command;
import hr.yeti.rudimentary.cli.ProjectLayout;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CreateNewProjectCommand implements Command {

  @Override
  public String name() {
    return "new-project";
  }

  @Override
  public String description() {
    return "Create new project.";
  }

  @Override
  public Map<String, String> options() {
    return Map.of(
        "groupId", "Set project's Maven groupId. If not set, --package value is used as a default.",
        "location", "Set absolute path to location where the project will be created. If omitted, current directory will be used.",
        "name", "Set project name.",
        "package", "Create project's root package. If omitted, app root package will be created."
    );
  }

  @Override
  public void execute(Map<String, String> arguments) {
    if (!arguments.containsKey("name")) {
      System.out.println("Please set project name by using option --name.");
      return;
    }

    String projectLocation = arguments.getOrDefault("location", new File("").getAbsolutePath());
    String rootPackage = arguments.getOrDefault("package", "app");
    String groupId = arguments.getOrDefault("groupId", rootPackage);

    rootPackage = groupId;

    ProjectLayout projectLayout = new ProjectLayout(projectLocation + "/" + arguments.get("name"), rootPackage);

    try {
      projectLayout.createNewFile(projectLayout.getProjectDir(), "pom.xml", pom(groupId, arguments.get("name"), rootPackage).getBytes(StandardCharsets.UTF_8));
      projectLayout.createNewFile(projectLayout.getProjectDir(), "run.sh", runShScript(rootPackage).getBytes(StandardCharsets.UTF_8));
      projectLayout.createNewFile(projectLayout.getProjectDir(), "debug.sh", debugShScript(rootPackage).getBytes(StandardCharsets.UTF_8));
      projectLayout.createNewFile(projectLayout.getRootPackageDir(), "Application.java", mainClass(rootPackage).getBytes(StandardCharsets.UTF_8));
      projectLayout.createNewFile(projectLayout.getSrcDir(), "module-info.java", moduleInfo(rootPackage).getBytes(StandardCharsets.UTF_8));
      projectLayout.createNewFile(projectLayout.getResourcesDir(), "config.properties", config().getBytes(StandardCharsets.UTF_8));
      projectLayout.createNewFile(projectLayout.getServicesDir(), "hr.yeti.rudimentary.http.spi.HttpEndpoint", null);
      projectLayout.createNewFile(projectLayout.getServicesDir(), "hr.yeti.rudimentary.mvc.spi.ViewEndpoint", null);
      projectLayout.createNewFile(projectLayout.getServicesDir(), "hr.yeti.rudimentary.context.spi.Instance", null);
      projectLayout.createNewFile(projectLayout.getServicesDir(), "hr.yeti.rudimentary.sql.spi.BasicDataSource", null);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

  }

  private String mainClass(String rootPackage) {
    return (rootPackage.length() > 0 ? ("package " + rootPackage + ";\n"
        + "\n") : "")
        + "import hr.yeti.rudimentary.server.Server;\n"
        + "import java.io.IOException;\n"
        + "\n"
        + "public class Application {\n"
        + "\n"
        + "  public static void main(String[] args) throws IOException {\n"
        + "    Server.start();\n"
        + "  }\n"
        + "}\n"
        + "";
  }

  private String moduleInfo(String rootPackage) {
    return "import hr.yeti.rudimentary.http.spi.HttpEndpoint;\n"
        + "\n"
        + "module " + rootPackage + " {\n"
        + "  requires hr.yeti.rudimentary.api;\n"
        + "  requires hr.yeti.rudimentary.server;\n"
        + "\n"
        + "  uses HttpEndpoint;\n"
        + "}\n"
        + "";
  }

  private String config() {
    return "# Server\n"
        + "server.port=8888\n"
        + "server.threadPoolSize=25\n"
        + "server.stopDelay=0";
  }

  private String pom(String groupId, String projectName, String rootPackage) {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
        + "  <modelVersion>4.0.0</modelVersion>\n"
        + "  <groupId>" + groupId + "</groupId>\n"
        + "  <artifactId>" + projectName + "</artifactId>\n"
        + "  <version>1.0-SNAPSHOT</version>\n"
        + "  <packaging>jar</packaging>\n"
        + "  <dependencies>\n"
        + "    <dependency>\n"
        + "      <groupId>hr.yeti.rudimentary</groupId>\n"
        + "      <artifactId>rudimentary-api</artifactId>\n"
        + "      <version>1.0-SNAPSHOT</version>\n"
        + "    </dependency>\n"
        + "    <dependency>\n"
        + "      <groupId>hr.yeti.rudimentary</groupId>\n"
        + "      <artifactId>rudimentary-server</artifactId>\n"
        + "      <version>1.0-SNAPSHOT</version>\n"
        + "    </dependency>\n"
        + "  </dependencies>\n"
        + "  <build>\n"
        + "    <plugins>\n"
        + "      <plugin>\n"
        + "        <groupId>org.apache.maven.plugins</groupId>\n"
        + "        <artifactId>maven-shade-plugin</artifactId>\n"
        + "        <version>3.2.1</version>\n"
        + "        <executions>\n"
        + "          <execution>\n"
        + "            <id>fatjar</id>\n"
        + "            <phase>package</phase>\n"
        + "            <goals>\n"
        + "              <goal>shade</goal>\n"
        + "            </goals>\n"
        + "          </execution>\n"
        + "        </executions>\n"
        + "        <configuration>\n"
        + "          <transformers>\n"
        + "            <transformer implementation=\"org.apache.maven.plugins.shade.resource.ManifestResourceTransformer\">\n"
        + "              <mainClass>" + rootPackage + ".Application</mainClass>\n"
        + "            </transformer>\n"
        + "            <transformer implementation=\"org.apache.maven.plugins.shade.resource.ServicesResourceTransformer\"/>\n"
        + "          </transformers>\n"
        + "        </configuration>\n"
        + "      </plugin>\n"
        + "      <plugin>\n"
        + "        <groupId>hr.yeti.rudimentary</groupId>\n"
        + "        <artifactId>rudimentary-maven-plugin</artifactId>\n"
        + "        <version>1.0-SNAPSHOT</version>\n"
        + "        <executions>\n"
        + "          <execution>\n"
        + "            <phase>compile</phase>\n"
        + "            <goals>\n"
        + "              <goal>auto-register</goal>\n"
        + "            </goals>\n"
        + "          </execution>\n"
        + "        </executions>\n"
        + "      </plugin>"
        + "      <plugin>\n"
        + "        <groupId>org.apache.maven.plugins</groupId>\n"
        + "        <artifactId>maven-compiler-plugin</artifactId>\n"
        + "        <version>3.8.0</version>\n"
        + "        <configuration>\n"
        + "          <source>11</source>\n"
        + "          <target>11</target>\n"
        + "        </configuration>\n"
        + "      </plugin> \n"
        + "    </plugins>\n"
        + "  </build>\n"
        + "</project>";
  }

  public String runShScript(String rootPackage) {
    return "mvn compile package exec:exec -Dexec.executable=\"java\" -Dexec.args=\"-classpath %classpath " + rootPackage + ".Application\"";
  }

  public String debugShScript(String rootPackage) {
    return "mvn compile package exec:exec -Dexec.executable=\"java\" -Dexec.args=\"-classpath %classpath -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044 " + rootPackage + ".Application\"";
  }
}
