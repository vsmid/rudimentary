# Logging

## Introduction
In order to monitor how the application/service is behaving a logging mechanism needs to be provided.
Rudimentary does not offer anything special here. Instead it relies on Java's own logging mechanism.
You can read more about Java logging [here](https://docs.oracle.com/en/java/javase/13/core/java-logging-overview.html).

## Default logging configuration
Rudimentary has default logging configuration stored inside `rudimentary-server` module in a file called `server-logging.properties`.
```properties
handlers=java.util.logging.ConsoleHandler
.level=INFO

java.util.logging.ConsoleHandler.level=INFO
java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter

hr.yeti.rudimentary.server.Server.level=INFO
hr.yeti.rudimentary.server.Server.handler=java.util.logging.ConsoleHandler
```

## Provide you own logging configuration
To provide you own logging configuration set `logging.propertiesFile` configuration property.
Default value is `classpath:server-logging.properties`.
If your configuration file resides in `src/main/resources` directory you must use `classpath:` prefix just like in a default setting.
To load configuration file from file system use absolute path.

## Logging inside HttpEndpoint
To make things simpler `HttpEndpoint` provides simple way to access logger via `HttpEndpoint#logger`.
For now, logging outside `HttpEndpoint` is done in a classic Java way with static `Logger` declaration inside class.
