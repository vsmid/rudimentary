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
    private static final String HTTP_FILTER_PROVIDERS = "hr.yeti.rudimentary.http.filter.spi.HttpFilter";
    private static final String VIEW_ENDPOINT_PROVIDERS = "hr.yeti.rudimentary.mvc.spi.ViewEndpoint";
    private static final String INSTANCE_PROVIDERS = "hr.yeti.rudimentary.context.spi.Instance";
    private static final String DATASOURCE_PROVIDERS = "hr.yeti.rudimentary.sql.spi.BasicDataSource";
    private static final String BEFORE_INTERCEPTOR_PROVIDERS = "hr.yeti.rudimentary.interceptor.spi.BeforeInterceptor";
    private static final String AFTER_INTERCEPTOR_PROVIDERS = "hr.yeti.rudimentary.interceptor.spi.AfterInterceptor";
    private static final String EVENT_LISTENER_PROVIDERS = "hr.yeti.rudimentary.events.spi.EventListener";

    private Path projectRootDir;
    private Path servicesDir;

    private String httpEndpointProvidersList;
    private String httpFilterProvidersList;
    private String viewEndpointProvidersList;
    private String instanceProvidersList;
    private String datasourceProvidersList;
    private String beforeInterceptorProviderList;
    private String afterInterceptorProviderList;
    private String eventListenerProviderList;

    public RegisterAsServiceProvider() {
        try {
            this.projectRootDir = new File("").toPath().toAbsolutePath();
            this.servicesDir = this.projectRootDir.resolve("src/main/resources/META-INF/services");
            this.httpEndpointProvidersList = readProviders(servicesDir.resolve(HTTP_ENDPOINT_PROVIDERS));
            this.httpFilterProvidersList = readProviders(servicesDir.resolve(HTTP_FILTER_PROVIDERS));
            this.viewEndpointProvidersList = readProviders(servicesDir.resolve(VIEW_ENDPOINT_PROVIDERS));
            this.instanceProvidersList = readProviders(servicesDir.resolve(INSTANCE_PROVIDERS));
            this.datasourceProvidersList = readProviders(servicesDir.resolve(DATASOURCE_PROVIDERS));
            this.beforeInterceptorProviderList = readProviders(servicesDir.resolve(BEFORE_INTERCEPTOR_PROVIDERS));
            this.afterInterceptorProviderList = readProviders(servicesDir.resolve(AFTER_INTERCEPTOR_PROVIDERS));
            this.eventListenerProviderList = readProviders(servicesDir.resolve(EVENT_LISTENER_PROVIDERS));

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
            } else if (content.contains("extends HttpFilter") && !content.contains("abstract")) {
                writeProvider(HTTP_FILTER_PROVIDERS, file);
            } else if (content.contains("implements ViewEndpoint<") && !content.contains("abstract")) {
                writeProvider(VIEW_ENDPOINT_PROVIDERS, file);
            } else if (content.contains("implements Instance") && !content.contains("abstract")) {
                writeProvider(INSTANCE_PROVIDERS, file);
            } else if (content.contains("extends BasicDataSource") && !content.contains("abstract")) {
                writeProvider(DATASOURCE_PROVIDERS, file);
            } else if (content.contains("implements BeforeInterceptor") && !content.contains("abstract")) {
                writeProvider(BEFORE_INTERCEPTOR_PROVIDERS, file);
            } else if (content.contains("implements AfterInterceptor") && !content.contains("abstract")) {
                writeProvider(AFTER_INTERCEPTOR_PROVIDERS, file);
            } else if (content.contains("implements EventListener") && !content.contains("abstract")) {
                writeProvider(EVENT_LISTENER_PROVIDERS, file);
            }
        }
        return FileVisitResult.CONTINUE;
    }

    private String readProviders(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        return new String(Files.readAllBytes(path));
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
            case HTTP_FILTER_PROVIDERS:
                alreadyRegistered = httpFilterProvidersList.contains(provider);
                break;
            case VIEW_ENDPOINT_PROVIDERS:
                alreadyRegistered = viewEndpointProvidersList.contains(provider);
                break;
            case INSTANCE_PROVIDERS:
                alreadyRegistered = instanceProvidersList.contains(provider);
                break;
            case DATASOURCE_PROVIDERS:
                alreadyRegistered = datasourceProvidersList.contains(provider);
                break;
            case BEFORE_INTERCEPTOR_PROVIDERS:
                alreadyRegistered = beforeInterceptorProviderList.contains(provider);
                break;
            case AFTER_INTERCEPTOR_PROVIDERS:
                alreadyRegistered = afterInterceptorProviderList.contains(provider);
                break;
            case EVENT_LISTENER_PROVIDERS:
                alreadyRegistered = eventListenerProviderList.contains(provider);
                break;
            default:
                break;
        }

        if (!alreadyRegistered) {
            Files.write(providers, (provider + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        }

    }
}
