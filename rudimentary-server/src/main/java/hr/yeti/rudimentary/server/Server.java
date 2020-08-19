package hr.yeti.rudimentary.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Context;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.filter.spi.HttpFilter;
import hr.yeti.rudimentary.security.spi.AuthMechanism;
import hr.yeti.rudimentary.server.context.DefaultContextProvider;
import hr.yeti.rudimentary.server.http.processor.HttpProcessor;
import hr.yeti.rudimentary.shutdown.spi.ShutdownHook;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.Executors;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

public class Server {

    private static final System.Logger LOGGER = System.getLogger(Server.class.getName());

    private HttpServer httpServer;

    private int stopDelay;
    private int port;
    private int threadPoolSize;

    private boolean sslEnabled;
    private String protocol;
    private boolean clientAuth;
    private String keyStore;
    private String keyStorePassword;
    private String trustStore;
    private String trustStorePassword;

    private Context context;
    private Config config;

    public Server() {
        ServiceLoader.load(Context.class)
            .stream()
            .filter(ctx -> ctx.get() instanceof DefaultContextProvider) // Force default context provider
            .findFirst()
            .ifPresent((ctx) -> {
                context = ctx.get();
                config = context.loadConfig();
            });
    }

    public Server start() {
        try {
            long start = System.currentTimeMillis();

            config.seal();
            context.initLogger();

            LOGGER.log(System.Logger.Level.INFO, "Starting server[Java PID={0}]...", String.valueOf(ProcessHandle.current().pid()));

            context.initialize();

            port = Config.provider().property("server.port").asInt();
            threadPoolSize = Config.provider().property("server.threadPoolSize").asInt();
            stopDelay = Config.provider().property("server.stopDelay").asInt();
            sslEnabled = Config.provider().property("server.ssl.enabled").asBoolean();
            protocol = Config.provider().property("server.ssl.protocol").value();
            keyStore = Config.provider().property("server.ssl.keyStore").value();
            keyStorePassword = Config.provider().property("server.ssl.keyStorePassword").value();
            trustStore = Config.provider().property("server.ssl.trustStore").value();
            trustStorePassword = Config.provider().property("server.ssl.trustStorePassword").value();
            clientAuth = Config.provider().property("server.ssl.clientAuth").asBoolean();

            // Create server instance
            if (sslEnabled) {
                httpServer = createHttpsServer();
            } else {
                httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            }

            httpServer.setExecutor(Executors.newFixedThreadPool(threadPoolSize));
            HttpContext context = httpServer.createContext("/", Instance.of(HttpProcessor.class));

            // Load authentication mechanism
            Instance.providersOf(AuthMechanism.class).stream()
                .filter(AuthMechanism::conditional)
                .findFirst()
                .ifPresent((authMechanism) -> {
                    context.setAuthenticator(authMechanism);
                });

            // Load filters
            Instance.providersOf(HttpFilter.class)
                .stream()
                .sorted(Comparator.comparingInt(HttpFilter::order))
                .forEach((filter) -> {
                    context.getFilters().add(filter);
                });

            ShutdownHook shutdownHook = Instance.of(ShutdownHook.class);
            if (Objects.nonNull(shutdownHook)) {
                Runtime.getRuntime().addShutdownHook(
                    new Thread(() -> {
                        shutdownHook.onShutdown();
                    })
                );
            }

            // Start server
            httpServer.start();

            // ServerInfoConsolePrinter.printRegisteredUriInfo(LOGGER);
            // ServerInfoConsolePrinter.printConfigProperties(LOGGER);
            LOGGER.log(System.Logger.Level.INFO, "Server started in {0} ms, listening on port {1}", new Object[]{
                (String.valueOf(System.currentTimeMillis() - start)), String.valueOf(port)
            });

            return this;
        } catch (IOException | GeneralSecurityException e) {
            throw new ServerStartupException(e.getMessage(), e);
        }
    }

    public void stop() {
        LOGGER.log(System.Logger.Level.INFO, "Stopping server...");

        this.httpServer.stop(stopDelay);
        this.context.destroy();

        LOGGER.log(System.Logger.Level.INFO, "Server stopped.");
    }

    private HttpServer createHttpsServer() throws IOException, NoSuchAlgorithmException, GeneralSecurityException {
        HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(port), 0);

        SSLContext sslContext = createSslContext();

        httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            @Override
            public void configure(HttpsParameters params) {
                // TODO Set all ssl parameters which can be configured
                SSLParameters sslparams = sslContext.getDefaultSSLParameters();
                sslparams.setNeedClientAuth(clientAuth);
                params.setSSLParameters(sslparams);
            }
        });

        return httpsServer;
    }

    private SSLContext createSslContext() throws GeneralSecurityException, IOException {
        KeyStore keystore = null;
        KeyManagerFactory keyManagerFactory = null;
        if (keyStore.length() > 0) {
            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            try ( InputStream in = new FileInputStream(keyStore)) {
                keystore.load(in, keyStorePassword.toCharArray());
            }
            keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, keyStorePassword.toCharArray());
        }

        KeyStore truststore = null;
        TrustManagerFactory trustManagerFactory = null;

        if (trustStore.length() > 0) {
            truststore = KeyStore.getInstance(KeyStore.getDefaultType());
            try ( InputStream in = new FileInputStream(trustStore)) {
                truststore.load(in, trustStorePassword.toCharArray());
            }
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(truststore);
        }

        SSLContext sslContext = SSLContext.getInstance(protocol);

        sslContext.init(
            Objects.isNull(keyManagerFactory) ? null : keyManagerFactory.getKeyManagers(),
            Objects.isNull(trustManagerFactory) ? null : trustManagerFactory.getTrustManagers(),
            new SecureRandom());

        return sslContext;
    }

}
