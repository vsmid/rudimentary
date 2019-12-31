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

public class RegisterAsServiceProvider extends SimpleFileVisitor<Path> {

    private static final String HTTP_ENDPOINT_PROVIDERS = "hr.yeti.rudimentary.http.spi.HttpEndpoint";
    private static final String HTTP_FILTER_PROVIDERS = "hr.yeti.rudimentary.http.filter.spi.HttpFilter";
    private static final String VIEW_ENDPOINT_PROVIDERS = "hr.yeti.rudimentary.mvc.spi.ViewEndpoint";
    private static final String INSTANCE_PROVIDERS = "hr.yeti.rudimentary.context.spi.Instance";
    private static final String DATASOURCE_PROVIDERS = "hr.yeti.rudimentary.sql.spi.BasicDataSource";
    private static final String BEFORE_INTERCEPTOR_PROVIDERS = "hr.yeti.rudimentary.interceptor.spi.BeforeInterceptor";
    private static final String AFTER_INTERCEPTOR_PROVIDERS = "hr.yeti.rudimentary.interceptor.spi.AfterInterceptor";
    private static final String EVENT_LISTENER_PROVIDERS = "hr.yeti.rudimentary.events.spi.EventListener";
    private static final String EXCEPTION_HANDLER_PROVIDERS = "hr.yeti.rudimentary.exception.spi.ExceptionHandler";
    private static final String AUTH_MECHANISM_PROVIDERS = "hr.yeti.rudimentary.security.spi.AuthMechanism";
    private static final String IDENTITY_STORE_PROVIDERS = "hr.yeti.rudimentary.security.spi.IdentityStore";
    private static final String IDENTITY_DETAILS_PROVIDERS = "hr.yeti.rudimentary.security.spi.IdentityDetails";
    private static final String HEALTH_CHECK_PROVIDERS = "hr.yeti.rudimentary.health.spi.HealthCheck";
    private static final String OBJECT_POOL_PROVIDERS = "hr.yeti.rudimentary.pooling.spi.ObjectPool";

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
    private String exceptionHandlerProviderList;
    private String authMechanismProviderList;
    private String identityStoreProviderList;
    private String identityDetailsProviderList;
    private String healthCheckProviderList;
    private String objectProviderList;

    public RegisterAsServiceProvider() {
        try {
            this.projectRootDir = new File("").toPath().toAbsolutePath();
            this.servicesDir = this.projectRootDir.resolve("src").resolve("main").resolve("resources").resolve("META-INF").resolve("services");
            this.httpEndpointProvidersList = readProviders(servicesDir.resolve(HTTP_ENDPOINT_PROVIDERS));
            this.httpFilterProvidersList = readProviders(servicesDir.resolve(HTTP_FILTER_PROVIDERS));
            this.viewEndpointProvidersList = readProviders(servicesDir.resolve(VIEW_ENDPOINT_PROVIDERS));
            this.instanceProvidersList = readProviders(servicesDir.resolve(INSTANCE_PROVIDERS));
            this.datasourceProvidersList = readProviders(servicesDir.resolve(DATASOURCE_PROVIDERS));
            this.beforeInterceptorProviderList = readProviders(servicesDir.resolve(BEFORE_INTERCEPTOR_PROVIDERS));
            this.afterInterceptorProviderList = readProviders(servicesDir.resolve(AFTER_INTERCEPTOR_PROVIDERS));
            this.eventListenerProviderList = readProviders(servicesDir.resolve(EVENT_LISTENER_PROVIDERS));
            this.exceptionHandlerProviderList = readProviders(servicesDir.resolve(EXCEPTION_HANDLER_PROVIDERS));
            this.authMechanismProviderList = readProviders(servicesDir.resolve(AUTH_MECHANISM_PROVIDERS));
            this.identityStoreProviderList = readProviders(servicesDir.resolve(IDENTITY_STORE_PROVIDERS));
            this.identityDetailsProviderList = readProviders(servicesDir.resolve(IDENTITY_DETAILS_PROVIDERS));
            this.healthCheckProviderList = readProviders(servicesDir.resolve(HEALTH_CHECK_PROVIDERS));
            this.objectProviderList = readProviders(servicesDir.resolve(OBJECT_POOL_PROVIDERS));

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
            } else if (content.contains("implements ExceptionHandler") && !content.contains("abstract")) {
                writeProvider(EXCEPTION_HANDLER_PROVIDERS, file);
            } else if (content.contains("extends AuthMechanism") && !content.contains("abstract")) {
                writeProvider(AUTH_MECHANISM_PROVIDERS, file);
            } else if (content.contains("implements IdentityStore") && !content.contains("abstract")) {
                writeProvider(IDENTITY_STORE_PROVIDERS, file);
            } else if (content.contains("implements IdentityDetails") && !content.contains("abstract")) {
                writeProvider(IDENTITY_DETAILS_PROVIDERS, file);
            } else if (content.contains("implements HealthCheck") && !content.contains("abstract")) {
                writeProvider(HEALTH_CHECK_PROVIDERS, file);
            } else if (content.contains("extends ObjectPool") && !content.contains("abstract")) {
                writeProvider(OBJECT_POOL_PROVIDERS, file);
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
        String filePath = file.toAbsolutePath().toString();

        int javaSourcesIndex = filePath.indexOf("java") + ("java" + File.separator).length();
        String canonicalProviderName = filePath.substring(javaSourcesIndex, filePath.lastIndexOf("."));

        String resolvedProviderName = String.join(".", canonicalProviderName.split(File.separator));
        return resolvedProviderName;
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
            case EXCEPTION_HANDLER_PROVIDERS:
                alreadyRegistered = exceptionHandlerProviderList.contains(provider);
                break;
            case AUTH_MECHANISM_PROVIDERS:
                alreadyRegistered = authMechanismProviderList.contains(provider);
                break;
            case IDENTITY_STORE_PROVIDERS:
                alreadyRegistered = identityStoreProviderList.contains(provider);
                break;
            case IDENTITY_DETAILS_PROVIDERS:
                alreadyRegistered = identityDetailsProviderList.contains(provider);
                break;
            case HEALTH_CHECK_PROVIDERS:
                alreadyRegistered = healthCheckProviderList.contains(provider);
                break;
            case OBJECT_POOL_PROVIDERS:
                alreadyRegistered = objectProviderList.contains(provider);
                break;
            default:
                break;
        }

        if (!alreadyRegistered) {
            Files.write(providers, (provider + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        }

    }
}
