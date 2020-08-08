package hr.yeti.rudimentary.autoregister.maven.plugin.mojo;

import hr.yeti.rudimentary.autoregister.maven.plugin.ProjectLayout;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Creates new project.
 *
 * @author vedransmid@yeti-it.hr
 */
@Mojo(name = "new-project", requiresProject = false)
public class CreateNewProjectMojo extends AbstractMojo {

    /**
     * Projects name.
     */
    @Parameter(property = "name", required = true)
    String name;

    /**
     * Projects location.
     */
    @Parameter(property = "location")
    String location;

    /**
     * Root package name.
     */
    @Parameter(property = "package")
    String pckg;

    /**
     * Projects Maven group id.
     */
    @Parameter(property = "groupId")
    String group;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (Objects.isNull(name)) {
            System.out.println("Please set project name by using option --name.");
            return;
        }

        String projectLocation = Objects.isNull(location) ? new File("").getAbsolutePath() : location;
        String rootPackage = Objects.isNull(pckg) ? "app" : pckg;
        String groupId = Objects.isNull(group) ? rootPackage : group;

        rootPackage = groupId;

        ProjectLayout projectLayout = new ProjectLayout(projectLocation + "/" + name, rootPackage);

        try {
            projectLayout.createNewFile(projectLayout.getProjectDir(), "pom.xml",
                pom(groupId, name, rootPackage).getBytes(StandardCharsets.UTF_8));
            projectLayout.createNewFile(projectLayout.getProjectDir(), "run.sh",
                runShScript(rootPackage).getBytes(StandardCharsets.UTF_8));
            projectLayout.createNewFile(projectLayout.getProjectDir(), "debug.sh",
                debugShScript(rootPackage).getBytes(StandardCharsets.UTF_8));
            projectLayout.createNewFile(projectLayout.getRootPackageDir(), "Application.java",
                mainClass(rootPackage).getBytes(StandardCharsets.UTF_8));
            projectLayout.createNewFile(projectLayout.getSrcDir(), "module-info.java",
                moduleInfo(rootPackage).getBytes(StandardCharsets.UTF_8));
            projectLayout.createNewFile(projectLayout.getResourcesDir(), "config.properties",
                config().getBytes(StandardCharsets.UTF_8));
            projectLayout.createNewFile(projectLayout.getServicesDir(), "hr.yeti.rudimentary.http.spi.HttpEndpoint", null);
            projectLayout.createNewFile(projectLayout.getServicesDir(), "hr.yeti.rudimentary.mvc.spi.ViewEndpoint", null);
            projectLayout.createNewFile(projectLayout.getServicesDir(), "hr.yeti.rudimentary.context.spi.Instance", null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private String mainClass(String rootPackage) {
        return (rootPackage.length() > 0 ? ("package " + rootPackage + ";\n" + "\n") : "")
            + "import hr.yeti.rudimentary.server.Server;\n" + "\n"
            + "public class Application {\n" + "\n" + "  public static void main(String[] args) {\n"
            + "    new Server().start();\n" + "  }\n" + "}\n" + "";
    }

    private String moduleInfo(String rootPackage) {
        return "import hr.yeti.rudimentary.http.spi.HttpEndpoint;\n" + "\n" + "module " + rootPackage + " {\n"
            + "  requires hr.yeti.rudimentary.api;\n" + "  requires hr.yeti.rudimentary.server;\n" + "\n"
            + "  uses HttpEndpoint;\n" + "}\n" + "";
    }

    private String config() {
        return "# Server\n" + "server.port=8888\n" + "server.threadPoolSize=25\n" + "server.stopDelay=0";
    }

    private String pom(String groupId, String projectName, String rootPackage) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
            + "  <modelVersion>4.0.0</modelVersion>\n"
            + "  <groupId>" + groupId + "</groupId>\n"
            + "  <artifactId>" + projectName + "</artifactId>\n"
            + "  <version>1.0-SNAPSHOT</version>\n"
            + "  <packaging>jar</packaging>\n"
            + "  <dependencyManagement>\n"
            + "    <dependencies>\n"
            + "      <dependency>\n"
            + "        <groupId>org.junit</groupId>\n"
            + "        <artifactId>junit-bom</artifactId>\n"
            + "        <version>5.5.2</version>\n"
            + "        <type>pom</type>\n"
            + "        <scope>import</scope>\n"
            + "      </dependency>\n"
            + "    </dependencies>\n"
            + "  </dependencyManagement>\n"
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
            + "   <dependency>\n"
            + "      <groupId>org.junit.jupiter</groupId>\n"
            + "      <artifactId>junit-jupiter-api</artifactId>\n"
            + "      <scope>test</scope>\n"
            + "    </dependency>\n"
            + "    <dependency>\n"
            + "      <groupId>org.junit.jupiter</groupId>\n"
            + "      <artifactId>junit-jupiter-engine</artifactId>\n"
            + "      <scope>test</scope>\n"
            + "    </dependency>\n"
            + "    <dependency>\n"
            + "      <groupId>org.junit.jupiter</groupId>\n"
            + "      <artifactId>junit-jupiter-params</artifactId>\n"
            + "      <scope>test</scope>\n"
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
            + "            <goals>\n"
            + "              <goal>register-providers</goal>\n"
            + "            </goals>\n"
            + "          </execution>\n"
            + "        </executions>\n"
            + "      </plugin>\n"
            + "      <plugin>\n"
            + "        <groupId>org.apache.maven.plugins</groupId>\n"
            + "        <artifactId>maven-compiler-plugin</artifactId>\n"
            + "        <version>3.8.1</version>\n"
            + "        <configuration>\n"
            + "          <source>14</source>\n"
            + "          <target>14</target>\n"
            + "          <compilerArgs>\n"
            + "             <arg>--enable-preview</arg>\n"
            + "          </compilerArgs>\n"
            + "        </configuration>\n"
            + "      </plugin> \n"
            + "      <plugin>\n"
            + "        <groupId>org.apache.maven.plugins</groupId>\n"
            + "        <artifactId>maven-surefire-plugin</artifactId>\n"
            + "        <version>3.0.0-M5</version>\n"
            + "        <configuration>\n"
            + "          <argLine>--enable-preview</argLine>\n"
            + "          <includes>**/*Test.java</includes>\n"
            + "        </configuration>\n"
            + "      </plugin>\n"
            + "      <plugin>\n"
            + "        <groupId>org.apache.maven.plugins</groupId>\n"
            + "        <artifactId>maven-failsafe-plugin</artifactId>\n"
            + "        <version>3.0.0-M5</version>\n"
            + "        <configuration>\n"
            + "          <argLine>--enable-preview</argLine>\n"
            + "          <includes>**/*IT.java</includes>\n"
            + "        </configuration>\n"
            + "      </plugin>\n"
            + "    </plugins>\n"
            + "  </build>\n"
            + "</project>";
    }

    public String runShScript(String rootPackage) {
        return "mvn -Dexec.executable=\"java\" -Dexec.args=\"-classpath %classpath " + rootPackage
            + ".Application\" clean package exec:exec";
    }

    public String debugShScript(String rootPackage) {
        return "mvn -Dexec.executable=\"java\" -Dexec.args=\"-classpath %classpath -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044 "
            + rootPackage + ".Application\" clean package exec:exec";
    }
}
