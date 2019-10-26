package hr.yeti.rudimentary.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class ProjectLayout {

  private Path projectDir;
  private Path rootPackageDir;
  private Path srcDir;
  private Path resourcesDir;
  private Path testDir;
  private Path testResourcesDir;
  private Path metaInfDir;
  private Path servicesDir;

  private ProjectLayout() {
  }

  public ProjectLayout(String projectLocation, String rootPackage) {
    this.projectDir = Path.of(projectLocation);

    this.srcDir = projectDir.resolve(Path.of("src", "main", "java"));
    this.testDir = projectDir.resolve(Path.of("src", "test", "java"));

    this.resourcesDir = projectDir.resolve(Path.of("src", "main", "resources"));
    this.testResourcesDir = projectDir.resolve(Path.of("src", "test", "resources"));

    this.metaInfDir = resourcesDir.resolve("META-INF");
    this.servicesDir = metaInfDir.resolve("services");

    this.rootPackageDir = projectDir.resolve(srcDir).resolve(parsePackage(rootPackage));

    createProjectLayout();
  }

  private void createProjectLayout() {
    if (!Files.exists(projectDir)) {
      try {
        Files.createDirectories(projectDir.resolve(srcDir).resolve(rootPackageDir));
        Files.createDirectories(projectDir.resolve(resourcesDir));
        Files.createDirectories(projectDir.resolve(testDir));
        Files.createDirectories(projectDir.resolve(testResourcesDir));
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  public Path parsePackage(String pckg) {
    Path pckgPath = Path.of("");
    if (pckg.contains(".")) {
      String[] paths = pckg.split("\\.");
      for (String path : paths) {
        pckgPath = pckgPath.resolve(path);
      }
    } else {
      pckgPath = pckgPath.resolve(pckg);
    }
    return pckgPath;
  }

  public void createNewFile(Path where, String filename, byte[] content) throws IOException {
    Path fullNewFilePath = where.resolve(filename);

    if (!Files.exists(where)) {
      Files.createDirectories(where);
    }

    Files.write(fullNewFilePath, Objects.nonNull(content) ? content : "".getBytes(), StandardOpenOption.CREATE_NEW);
  }

  public Path getProjectDir() {
    return projectDir;
  }

  public Path getRootPackageDir() {
    return rootPackageDir;
  }

  public Path getSrcDir() {
    return srcDir;
  }

  public Path getResourcesDir() {
    return resourcesDir;
  }

  public Path getMetaInfDir() {
    return metaInfDir;
  }

  public Path getServicesDir() {
    return servicesDir;
  }

  public Path getTestDir() {
    return testDir;
  }

  public Path getTestResourcesDir() {
    return testResourcesDir;
  }

}
