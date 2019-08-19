# Rudimentary framework

A small, simple and opinionated Java based web services/applications framework. 

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
* Maven

## Getting started - writing Hello World

Since there are no publicly available artifacts in jcenter or maven central of Rudimentary framework yet, here are the steps to easily create Rudimentary project:

1. Clone this repository from your terminal (git clone https://github.com/vsmid/rudimentary.git)
2. Go to the root of cloned project
3. Execute `mvn clean install`
4. Execute `java -jar rudimentary-cli/target/rudimentary-cli-1.0-SNAPSHOT.jar new-project --name hello-world`
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

6. If you disabled/removed rudimentary-maven-plugin in project's pom.xml add **app.HelloWorldEndpoint** entry to **src/main/resources/META-INF/services/hr.yeti.rudimentary.http.spi.HttpEndpoint** file.
7. To run your application, you can execute either `run.sh` or `debug.sh` script found in the root of the project. Debug listens on port 1044 in case you want to attach debugger.
8. Using default values, `curl http://localhost:8888/helloWorldEndpoint` should return **Hello World!** response

## Javadoc

Check [rudimentary-api](./rudimentary-api) module for Javadoc.

## Showcase module

Make sure to check [rudimentary-demo](./rudimentary-demo/src/main/java/hr/yeti/rudimentary/demo/endpoint) for code examples describing what is stated in **notable features** section.
Showcase can be run by executing `run.sh` script found in the root of the rudimentary-demo module.

## Roadmap

* Dynamic module loading/reloading while using rudimentary-runner module and Jigsaw
* Add MVC caching policy settings
* Add MVC escaping strategies, e.g. html, js, css, attributes, read OWASP
* Add Security LoginForm authentication option
* Add Security JWT authentication option
* Add Security CSRF protection - Basic, draft version available
* Add Security CORS handling - Basic, draft version available
* Add Sql rollback options - basic draft done.
* Add HttpEndpoint async() method for async execution
* Add Metrics (Time, Gauge, Count)
* Add ApiDoc - read OpenApi - added very basic opinionated extension
* Add Tracing - read OpenTracing
* Finish tests and README docs
* More validation constraint definitions and implementations
* JDBC - use HikariCP - basic impl, done.
* Add fallback, bulkead, retry, backpressure handlers.
* Logger should be moved from HttpEndpoint to allow access from anywhere
* System properties listed are missing ones from system and environment
* Add internationalization for messages etc.
* Create test package within api moudule to help when writing tests, e.g. start server, load sql script utility, context mock etc. - Basic config and context mocks for now.
* Add https server - added basic, draft version
