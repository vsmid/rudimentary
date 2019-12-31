# Shutdown hook

## Introduction
If you wish to add some loginc when application/service is shutting down you need to add a shutdown hook mechanism.

## Adding shutdown hook
To add your own shutdown hook mechanism you need to implement `hr.yeti.rudimentary.shutdown.spi.ShutdownHook` interface.
There should be only one `ShutdownHook` provider per application/service and you can register it in `src/main/resources/META-INF/services/hr.yeti.rudimentary.shutdown.spi.ShutdownHook` file. This however, rudimentary-maven-plugin already automatically does for you.

