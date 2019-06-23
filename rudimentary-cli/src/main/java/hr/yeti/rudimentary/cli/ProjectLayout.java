package hr.yeti.rudimentary.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class ProjectLayout {

  private Path projectRootPath;
  private Path rootPackagePath;

  private Path srcDir;
  private Path resourcesDir;
  private Path testDir;
  private Path testResourcesDir;
  private Path metaInfDir;
  private Path servicesDir;

  private ProjectLayout() {
  }

  public ProjectLayout(String projectLocation, String rootPackage) {
    this.projectRootPath = Paths.get(projectLocation);

    this.srcDir = projectRootPath.resolve(Paths.get("src", "main", "java"));
    this.testDir = projectRootPath.resolve(Paths.get("src", "test", "java"));

    this.resourcesDir = projectRootPath.resolve(Paths.get("src", "main", "resources"));
    this.testResourcesDir = projectRootPath.resolve(Paths.get("src", "test", "resources"));

    this.metaInfDir = resourcesDir.resolve("META-INF");
    this.servicesDir = metaInfDir.resolve("services");

    this.rootPackagePath = projectRootPath.resolve(srcDir).resolve(parsePackage(rootPackage));

    createProjectLayout();
  }

  private void createProjectLayout() {
    if (!Files.exists(projectRootPath)) {
      try {
        Files.createDirectories(projectRootPath.resolve(srcDir).resolve(rootPackagePath));
        Files.createDirectories(projectRootPath.resolve(resourcesDir));
      } catch (IOException ex) {;
        ex.printStackTrace();
      }
    }
  }

  public Path parsePackage(String pckg) {
    Path pckgPath = Paths.get("");
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

    if (Objects.nonNull(content)) {
      Files.write(fullNewFilePath, content, StandardOpenOption.CREATE_NEW);
    }

  }

  public Path getProjectRootPath() {
    return projectRootPath;
  }

  public Path getRootPackagePath() {
    return rootPackagePath;
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
