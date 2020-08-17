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
 * Creates new extension project.
 *
 * @author vedransmid@yeti-it.hr
 */
@Mojo(name = "new-extension", requiresProject = false)
public class CreateExtensionMojo extends AbstractMojo {

    /**
     * Extension name.
     */
    @Parameter(property = "name", required = true)
    String name;

    /**
     * Extension location.
     */
    @Parameter(property = "location")
    String location;

    /**
     * Extension Maven group id.
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
        String rootPackage = "hr.yeti.rudimentary.exts.custom";
        String groupId = Objects.isNull(group) ? rootPackage : group;

        rootPackage = groupId;

        ProjectLayout projectLayout = new ProjectLayout(projectLocation + "/" + name, rootPackage);

        try {
            projectLayout.createNewFile(projectLayout.getProjectDir(), "pom.xml",
                pom(groupId, name, rootPackage).getBytes(StandardCharsets.UTF_8));
            projectLayout.createNewFile(projectLayout.getSrcDir(), "module-info.java",
                moduleInfo(rootPackage).getBytes(StandardCharsets.UTF_8));
            projectLayout.createNewFile(projectLayout.getResourcesDir(), "config.properties",
                config().getBytes(StandardCharsets.UTF_8));
            projectLayout.getServicesDir().toFile().mkdirs();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private String moduleInfo(String rootPackage) {
        return "module " + rootPackage + " {\n"
            + "    requires hr.yeti.rudimentary.api;\n"
            + "}\n"
            + "";
    }

    private String config() {
        return "";
    }

    private String pom(String groupId, String projectName, String rootPackage) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
            + "    <modelVersion>4.0.0</modelVersion>\n"
            + "    <parent>\n"
            + "        <groupId>hr.yeti.rudimentary.exts</groupId>\n"
            + "        <artifactId>rudimentary-exts</artifactId>\n"
            + "        <version>1.0-SNAPSHOT</version>\n"
            + "    </parent>  \n"
            + "    <groupId>" + groupId + "</groupId>\n"
            + "    <artifactId>" + projectName + "</artifactId>\n"
            + "    <version>1.0-SNAPSHOT</version>\n"
            + "    <packaging>jar</packaging>\n"
            + "    <dependencies>\n"
            + "        <dependency>\n"
            + "            <groupId>hr.yeti.rudimentary</groupId>\n"
            + "            <artifactId>rudimentary-api</artifactId>\n"
            + "            <version>1.0-SNAPSHOT</version>\n"
            + "        </dependency>\n"
            + "    </dependencies>\n"
            + "    <build>\n"
            + "        <plugins>\n"
            + "            <plugin>\n"
            + "                <groupId>org.apache.maven.plugins</groupId>\n"
            + "                <artifactId>maven-compiler-plugin</artifactId>\n"
            + "                <configuration>\n"
            + "                    <source>14</source>\n"
            + "                    <target>14</target>\n"
            + "                    <compilerArgs>\n"
            + "                        <arg>--enable-preview</arg>\n"
            + "                    </compilerArgs>\n"
            + "                </configuration>\n"
            + "            </plugin>\n"
            + "        </plugins>\n"
            + "    </build>\n"
            + "</project>";
    }

}
