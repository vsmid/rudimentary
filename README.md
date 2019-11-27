# Rudimentary framework

A small, simple and opinionated Java based web services/applications framework. 

# Table of contents (in progress...)
* Rudimentary API
  * [Configuration](rudimentary-api/src/main/java/hr/yeti/rudimentary/config/README.md) :white_check_mark:
  * [Context](rudimentary-api/src/main/java/hr/yeti/rudimentary/context/README.md) :white_check_mark:
  * [Http](rudimentary-api/src/main/java/hr/yeti/rudimentary/http/README.md) :ant:
  * [Events](rudimentary-api/src/main/java/hr/yeti/rudimentary/events/README.md)
  * [Interceptors](rudimentary-api/src/main/java/hr/yeti/rudimentary/interceptor/README.md)
  * [Security](rudimentary-api/src/main/java/hr/yeti/rudimentary/security/README.md)
  * [Validation](rudimentary-api/src/main/java/hr/yeti/rudimentary/validation/README.md)
  * [Exceptions](rudimentary-api/src/main/java/hr/yeti/rudimentary/exception/README.md)
  * [MVC](rudimentary-api/src/main/java/hr/yeti/rudimentary/mvc/README.md)
  * [Sql](rudimentary-api/src/main/java/hr/yeti/rudimentary/sql/README.md)
  * [Testing](rudimentary-api/src/main/java/hr/yeti/rudimentary/test/README.md)
  * [Email](rudimentary-api/src/main/java/hr/yeti/rudimentary/email/README.md)
  * [Health check](rudimentary-api/src/main/java/hr/yeti/rudimentary/health/README.md)
  * [Pooling](rudimentary-api/src/main/java/hr/yeti/rudimentary/pooling/README.md)
  * [Shutdown hook](rudimentary-api/src/main/java/hr/yeti/rudimentary/shutdown/README.md)
  * Logging
* [Rudimentary CLI](rudimentary-cli/README.md)
* [Rudimentary Extensions](rudimentary-exts/README.md)
  * [Pebble MVC extension](rudimentary-exts/rudimentary-mvc-pebble-ext/README.md)
* [Rudimentary Maven Plugin](rudimentary-maven-plugin/README.md)
* [Rudimentary Server](rudimentary-server/README.md)

## Current status

Closing in on the initial release feature set. Slowly but steadily adding documentation. Not yet ready to be used in production environment. Use it for fast prototyping, mocks etc. 

## Introduction

It all started as a playground for the new Java module system and practicing some good old patterns.
The goal of this framework it to produce the simplest possible Java framework by trying to use only JDK (notable exceptions for now are libraries dealing with JSON conversions and default HikariCP connection pooling). It avoids using annotations and reflection as much as possible.
This framework is also opinionated and promotes flat application/service design. This means there is no strict separation on controller, service and repository layers. One web method implementation is contained within a single Java file. Of course, there is nothing preventing you to do it your own way :)
This framework also promotes static access to various resources such as sql, email and object instances by using Java's ServiceLoader utility to reduce boilerplate code as much as possible. The goal is to not have object instances as properties in classes. If you want to use something, it should be available in a static way.

## Notable features

* Easy and simple configuration via properties file, environment and system properties
* Simple IoC container mechanism
* Simple and efficient validation pattern
* Simple SQL access
* Simple email access
* Simple MVC framework
* Simple application/service security configuration
* Simple event system
* It is easy to extends framework with your own custom stuff (look at the exts module)
* Simple, extensible security model + CSRF and CORS handling.

Majority of features are a work in progress but are usable and show the intent.

## Prerequisites

* Java 11+
* Maven - Set M2_HOME environment variable or maven.home system property

## Getting started - writing Hello World

Since there are no publicly available artifacts in jcenter or maven central of Rudimentary framework yet, here are the steps to easily create Rudimentary project:

1. Clone this repository from your terminal (git clone https://github.com/vsmid/rudimentary.git)
2. Go to the root of cloned project
3. Execute `mvn clean install`. This command will build rudimentary-cli-1.0-SNAPSHOT.jar inside rudimentary-cli/target directory.
4. Execute `java -jar PATH_TO_rudimentary-cli-1.0-SNAPSHOT.jar new-project --name hello-world`
  
    *Hint: you can also choose to set a different location using --location parameter.*

5. Go to **src/main/java/app** directory and create Java class **HelloWorldEndpoint** like this:

```java
package app;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;

public class HelloWorldEndpoint implements HttpEndpoint<Empty, Text> {

  @Override
  public Text response(Request<Empty> request) {
    return new Text("Hello World!");
  }

}
```
  *Hint: you can also use rudimentary-cli to create new http endpoint.*

6. If you disabled/removed rudimentary-maven-plugin in project's pom.xml add **app.HelloWorldEndpoint** entry to **src/main/resources/META-INF/services/hr.yeti.rudimentary.http.spi.HttpEndpoint** file otherwise skip this step.
7. To run your application, execute `java -jar PATH_TO_rudimentary-cli-1.0-SNAPSHOT.jar run` from inside hello-world project.
You can also execute application by running either `run.sh` or `debug.sh` script found in the root of the generated project. Debug listens on port 1044 by default in case you want to attach debugger.
8. Using default values, `curl http://localhost:8888/helloWorldEndpoint` should return **Hello World!** response

## Javadoc

Check [rudimentary-api](./rudimentary-api) module for Javadoc.

## Showcase module

Make sure to check [rudimentary-demo](./rudimentary-demo/src/main/java/hr/yeti/rudimentary/demo/endpoint) for code examples describing what is stated in **notable features** section.
Showcase can be run by using rudimentary-cli tool(see how hello-world application is run) or by executing `run.sh` script found in the root of the rudimentary-demo module.
