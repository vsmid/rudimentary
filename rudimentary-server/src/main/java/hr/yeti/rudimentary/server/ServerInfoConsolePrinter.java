package hr.yeti.rudimentary.server;

import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.server.http.HttpEndpointContextProvider;
import java.util.Optional;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ServerInfoConsolePrinter {

    public static void printRegisteredUriInfo(Logger logger) {
        System.out.println(System.lineSeparator());

        logger.log(Level.INFO, "Registered uris: {0} {1}",
            new Object[]{
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
                    .append(Optional.ofNullable(e.description()).map(desc -> " -> " + desc).orElse(""))
                    .toString())
                    .collect(Collectors.joining(System.lineSeparator()))
            });
    }

    public static void printConfigProperties(Logger logger) {
        StringBuilder propertiesList = new StringBuilder();
        TreeMap props = new TreeMap(Config.provider().getProperties());

        props.forEach((key, value) -> {
            propertiesList.append(key).append("=").append(value).append(System.lineSeparator());
        });

        System.out.println(System.lineSeparator());

        logger.log(Level.INFO, "{0} {1}{2}", new Object[]{
            "Configuration properties:",
            System.lineSeparator() + System.lineSeparator(),
            propertiesList.toString()
        });
    }
}
