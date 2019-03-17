package hr.yeti.rudimentary.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Context;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.security.spi.AuthMechanism;
import hr.yeti.rudimentary.server.http.HttpEndpointContextProvider;
import hr.yeti.rudimentary.server.http.processor.HttpProcessor;
import hr.yeti.rudimentary.server.http.session.HttpSessionCreatingFilter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Server {

  private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

  private HttpServer httpServer;

  private int stopDelay;
  private int port;
  private int threadPoolSize;
  private boolean createSession;

  private Server() throws IOException {
    LogManager.getLogManager().readConfiguration(
        this.getClass().getClassLoader().getResourceAsStream("server-logging.properties")
    );

    ServiceLoader.load(Context.class)
        .findFirst()
        .get()
        .initialize();
  }

  public static Server start() throws IOException {
    long start = System.currentTimeMillis();

    LOGGER.info("Starting server...");

    Server server = new Server();

    server.port = Config.provider().property("server.port").asInt();
    server.threadPoolSize = Config.provider().property("server.threadPoolSize").asInt();
    server.stopDelay = Config.provider().property("server.stopDelay").asInt();
    server.createSession = Config.provider().property("session.create").asBoolean();

    // Create server instance
    server.httpServer = HttpServer.create(new InetSocketAddress(server.port), server.threadPoolSize);
    HttpContext context = server.httpServer.createContext("/", new HttpProcessor(
        server.threadPoolSize
    ));

    // Load authentication mechanism
    Instance.providersOf(AuthMechanism.class).stream()
        .findFirst()
        .ifPresent((authMechanism) -> {
          if (authMechanism.enabled()) {
            context.setAuthenticator(authMechanism);
          }
        });

    // Load filters
    if (server.createSession) {
      context.getFilters().add(new HttpSessionCreatingFilter());
    }

    // Start server
    server.httpServer.start();

    printRegisteredUriInfo();

    LOGGER.log(Level.INFO, "Server started in {0} ms, listening on port {1}", new Object[]{
      (String.valueOf(System.currentTimeMillis() - start)), String.valueOf(server.port)
    });

    return server;
  }

  public void stop() {
    this.httpServer.stop(stopDelay);
  }

  private static void printRegisteredUriInfo() {
    LOGGER.log(Level.INFO, "Registered uris: {0} {1}", new Object[]{
      System.lineSeparator(),
      Instance.of(HttpEndpointContextProvider.class).getRegisteredUris()
      .stream()
      .map(e -> new StringBuilder()
      .append("\t")
      .append(e.httpMethod().toString())
      .append(" ")
      .append(!e.path().toString().startsWith("/") ? "/" : "")
      .append(e.path().toString())
      .append(" ")
      .append(e.description().map(desc -> " -> " + desc).orElse(""))
      .toString())
      .collect(Collectors.joining(System.lineSeparator()))
    });
  }
}
