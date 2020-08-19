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
Default value is set to `classpath:server-logging.properties`.
If your configuration file resides in `src/main/resources` directory you must use `classpath:` prefix just like in a default setting.
To load configuration file from file system use absolute path.

### Logging inside instances
Convenient method for logger access is provided via `hr.yeti.rudimentary.context.spi.Instance#logger` method. 
Any class implementing `hr.yeti.rudimentary.context.spi.Instance` automatically gains access to default logger.
For more information about this logger check [System.Logger](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/System.Logger.html).

## Using another logging framework
If you choose to use another logging framework then the above configuration instructions do not apply. You must use configuration specific to your new logging framework.
