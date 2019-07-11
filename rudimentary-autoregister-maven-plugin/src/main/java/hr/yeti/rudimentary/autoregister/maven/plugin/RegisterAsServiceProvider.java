package hr.yeti.rudimentary.autoregister.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO Handle classes which extend framework's SPI's
// TODO Handle interfaces which extend framework's SPI's
// TODO Add other providers, e.g.EventListener, AuthMechanism, HttpFilter etc.

public class RegisterAsServiceProvider extends SimpleFileVisitor<Path> {

  private static final String HTTP_ENDPOINT_PROVIDERS = "hr.yeti.rudimentary.http.spi.HttpEndpoint";
  private static final String VIEW_ENDPOINT_PROVIDERS = "hr.yeti.rudimentary.mvc.spi.ViewEndpoint";
  private static final String INSTANCE_PROVIDERS = "hr.yeti.rudimentary.context.spi.Instance";

  private Path projectRootDir;
  private Path servicesDir;

  private String httpEndpointProvidersList;
  private String viewEndpointProvidersList;
  private String instanceProvidersList;

  public RegisterAsServiceProvider() {
    try {
      this.projectRootDir = new File("").toPath().toAbsolutePath();
      this.servicesDir = Path.of(projectRootDir.toString(), "/src/main/resources/META-INF/services");

      this.httpEndpointProvidersList = readProviders(servicesDir.resolve(HTTP_ENDPOINT_PROVIDERS));
      this.viewEndpointProvidersList = readProviders(servicesDir.resolve(VIEW_ENDPOINT_PROVIDERS));
      this.instanceProvidersList = readProviders(servicesDir.resolve(INSTANCE_PROVIDERS));

    } catch (IOException ex) {
      Logger.getLogger(RegisterAsServiceProvider.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    if (file.toString().endsWith(".java") && !file.toString().contains("module-info.java")) {

      byte[] readAllBytes = Files.readAllBytes(file);
      String content = new String(readAllBytes);

      if (content.contains("implements HttpEndpoint<") && !content.contains("abstract")) {
        writeProvider(HTTP_ENDPOINT_PROVIDERS, file);
      } else if (content.contains("implements ViewEndpoint<") && !content.contains("abstract")) {
        writeProvider(VIEW_ENDPOINT_PROVIDERS, file);
      } else if (content.contains("implements Instance") && !content.contains("abstract")) {
        writeProvider(INSTANCE_PROVIDERS, file);
      }
    }
    return FileVisitResult.CONTINUE;
  }

  private String readProviders(Path path) throws IOException {
    if (!Files.exists(path)) {
      Files.createFile(path);
    }
    return Files.readString(path);
  }

  private String extractProvider(Path file) {
    String[] parts = file.toAbsolutePath().toString().split("src/main/java");
    return parts[1].replaceAll("/", ".").replace(".java", "").substring(1);
  }

  private void writeProvider(String providerType, Path file) throws IOException {
    Path providers = servicesDir.resolve(providerType);
    String provider = extractProvider(file);

    boolean alreadyRegistered = false;

    switch (providerType) {
      case HTTP_ENDPOINT_PROVIDERS:
        alreadyRegistered = httpEndpointProvidersList.contains(provider);
        break;
      case VIEW_ENDPOINT_PROVIDERS:
        alreadyRegistered = viewEndpointProvidersList.contains(provider);
        break;
      case INSTANCE_PROVIDERS:
        alreadyRegistered = instanceProvidersList.contains(provider);
        break;
      default:
        break;
    }

    if (!alreadyRegistered) {
      Files.write(providers, (provider + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
    }

  }
}
