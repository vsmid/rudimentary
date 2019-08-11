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
import hr.yeti.rudimentary.server.http.processor.HttpProcessor;
import hr.yeti.rudimentary.server.resources.ClasspathResource;
import hr.yeti.rudimentary.shutdown.spi.ShutdownHook;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

public class Server {

  private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

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

  private Server() throws IOException {
    LogManager.getLogManager()
        .readConfiguration(
            new ClasspathResource("server-logging.properties").get()
        );

    ServiceLoader.load(Context.class)
        .findFirst()
        .ifPresent((ctx) -> {
          this.context = ctx;
          this.context.initialize();
        });
  }

  public static Server start() {

    try {
      long start = System.currentTimeMillis();

      LOGGER.info("Starting server...");

      Server server = new Server();

      server.port = Config.provider().property("server.port").asInt();
      server.threadPoolSize = Config.provider().property("server.threadPoolSize").asInt();
      server.stopDelay = Config.provider().property("server.stopDelay").asInt();
      server.sslEnabled = Config.provider().property("server.ssl.enabled").asBoolean();
      server.protocol = Config.provider().property("server.ssl.protocol").value();
      server.keyStore = Config.provider().property("server.ssl.keyStore").value();
      server.keyStorePassword = Config.provider().property("server.ssl.keyStorePassword").value();
      server.trustStore = Config.provider().property("server.ssl.trustStore").value();
      server.trustStorePassword = Config.provider().property("server.ssl.trustStorePassword").value();
      server.clientAuth = Config.provider().property("server.ssl.clientAuth").asBoolean();

      // Create server instance
      if (server.sslEnabled) {
        server.httpServer = createHttpsServer(server);
      } else {
        server.httpServer = HttpServer.create(new InetSocketAddress(server.port), server.threadPoolSize);
      }

      HttpContext context = server.httpServer.createContext("/", Instance.of(HttpProcessor.class));

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
          .forEach((filter) -> {
            context.getFilters().add(filter);
          });

      // Start server
      server.httpServer.start();

      ServerInfoConsolePrinter.printRegisteredUriInfo(LOGGER);
      ServerInfoConsolePrinter.printConfigProperties(LOGGER);

      LOGGER.log(Level.INFO, "Server started in {0} ms, listening on port {1}", new Object[]{
        (String.valueOf(System.currentTimeMillis() - start)), String.valueOf(server.port)
      });

      return server;
    } catch (IOException | GeneralSecurityException e) {
      throw new ServerStartupException(e.getMessage(), e);
    }
  }

  public void stop() {
    LOGGER.log(Level.INFO, "Stopping server...");

    ShutdownHook shutdownHook = Instance.of(ShutdownHook.class);

    this.httpServer.stop(stopDelay);

    Thread shutdownHookThread = null;
    
    if (Objects.nonNull(shutdownHook)) {
      Runtime.getRuntime().addShutdownHook(
          new Thread(() -> {
            shutdownHook.onShutdown();
            destroyContext();
          })
      );
    } else {
      destroyContext();
    }
  }

  private void destroyContext() {
    this.context.destroy();
    LOGGER.log(Level.INFO, "Server stopped.");
  }

  private static HttpServer createHttpsServer(Server server) throws IOException, NoSuchAlgorithmException, GeneralSecurityException {
    HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(server.port), server.threadPoolSize);

    SSLContext sslContext = createSslContext(server);

    httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
      @Override
      public void configure(HttpsParameters params) {
        // TODO Set all ssl parameters which can be configured
        SSLParameters sslparams = sslContext.getDefaultSSLParameters();
        sslparams.setNeedClientAuth(server.clientAuth);
        params.setSSLParameters(sslparams);
      }
    });

    return httpsServer;
  }

  private static SSLContext createSslContext(Server server) throws GeneralSecurityException, IOException {

    KeyStore keystore = null;
    KeyManagerFactory keyManagerFactory = null;
    if (server.keyStore.length() > 0) {
      keystore = KeyStore.getInstance(KeyStore.getDefaultType());
      try (InputStream in = new FileInputStream(server.keyStore)) {
        keystore.load(in, server.keyStorePassword.toCharArray());
      }
      keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      keyManagerFactory.init(keystore, server.keyStorePassword.toCharArray());
    }

    KeyStore truststore = null;
    TrustManagerFactory trustManagerFactory = null;
    if (server.trustStore.length() > 0) {
      truststore = KeyStore.getInstance(KeyStore.getDefaultType());
      try (InputStream in = new FileInputStream(server.trustStore)) {
        truststore.load(in, server.trustStorePassword.toCharArray());
      }
      trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

      trustManagerFactory.init(truststore);
    }

    SSLContext sslContext = SSLContext.getInstance(server.protocol);

    sslContext.init(
        Objects.isNull(keyManagerFactory) ? null : keyManagerFactory.getKeyManagers(),
        Objects.isNull(trustManagerFactory) ? null : trustManagerFactory.getTrustManagers(),
        new SecureRandom());

    return sslContext;
  }

}
