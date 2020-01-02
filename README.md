# Rudimentary framework

A small, simple and opinionated Java based web services/applications framework. 

# Table of contents (in progress...)
* Rudimentary API
  * [Configuration](rudimentary-api/src/main/java/hr/yeti/rudimentary/config/README.md) :white_check_mark:
  * [Context](rudimentary-api/src/main/java/hr/yeti/rudimentary/context/README.md) :white_check_mark:
  * [Http](rudimentary-api/src/main/java/hr/yeti/rudimentary/http/README.md) :white_check_mark:
  * [Events](rudimentary-api/src/main/java/hr/yeti/rudimentary/events/README.md) :white_check_mark:
  * [Interceptors](rudimentary-api/src/main/java/hr/yeti/rudimentary/interceptor/README.md) :white_check_mark:
  * [Security](rudimentary-api/src/main/java/hr/yeti/rudimentary/security/README.md) :white_check_mark:
  * [Validation](rudimentary-api/src/main/java/hr/yeti/rudimentary/validation/README.md) :white_check_mark:
  * [Exception handling](rudimentary-api/src/main/java/hr/yeti/rudimentary/exception/README.md) :white_check_mark:
  * [MVC](rudimentary-api/src/main/java/hr/yeti/rudimentary/mvc/README.md) :white_check_mark:
  * [Sql](rudimentary-api/src/main/java/hr/yeti/rudimentary/sql/README.md) :white_check_mark:
  * [Testing](rudimentary-api/src/main/java/hr/yeti/rudimentary/test/README.md) :white_check_mark:
  * [Email](rudimentary-api/src/main/java/hr/yeti/rudimentary/email/README.md) :white_check_mark:
  * [Health check](rudimentary-api/src/main/java/hr/yeti/rudimentary/health/README.md) :white_check_mark:
  * [Pooling](rudimentary-api/src/main/java/hr/yeti/rudimentary/pooling/README.md) :white_check_mark:
  * [Shutdown hook](rudimentary-api/src/main/java/hr/yeti/rudimentary/shutdown/README.md) :white_check_mark:
  * [Logging](rudimentary-api/src/main/java/hr/yeti/rudimentary/logging/README.md) :white_check_mark:
* Rudimentary extensions
  * [Pebble MVC extension](rudimentary-exts/rudimentary-mvc-pebble-ext/README.md) :white_check_mark:

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

1. Clone this repository from your terminal (`git clone https://github.com/vsmid/rudimentary.git`)
2. Go to the root of cloned project (`cd rudimentary`)
3. Execute `mvn install` command
4. Leave rudimentary directory (`cd ..`)
5. Execute `mvn hr.yeti.rudimentary:rudimentary-maven-plugin:1.0-SNAPSHOT:new-project -Dname=hello-world`command
6. Go to created *hello-world* directory (`cd hello-world`) and execute `mvn rudi:new-endpoint -DclassName=HelloWorldEndpoint -Dpackage=app`. This will create new Java class named *HelloWorldEndpoint* in *src/main/java/app* directory. You can edit `HelloWorldEndpoint#response` method if you wish.
7. To run your application, execute `mvn rudi:run` from inside *hello-world* directory (if you are using IDE this goal should be available on click in Maven perspective). This will run application in debug mode so you can attach debugger at any time. Also, if you press enter while application is running, you can run tests automatically.
You can also execute application by running either `run.sh` or `debug.sh` script found in the root of the generated project. Debug listens on port 1044 by default in case you want to attach debugger.
8. Using default values, `curl http://localhost:8888/helloWorldEndpoint` should return **null** or new value set by you as a response.

*Full script for the first time project creation - (copy & paste to terminal)*
```script
git clone https://github.com/vsmid/rudimentary.git
cd ./rudimentary
mvn install
cd ..
mvn hr.yeti.rudimentary:rudimentary-maven-plugin:1.0-SNAPSHOT:new-project -Dname=hello-world
cd hello-world
mvn rudi:new-endpoint -DclassName=HelloWorldEndpoint -Dpackage=app
```

*If you already cloned and installed Rudimentary start from step 5.*

## Rudimentary Maven plugin goals and docs
Run `mvn help:describe -Dplugin=rudi` to see plugin goals and documentation if you are within some Rudimentary project.
If you are not within some Rudimentary project, run `mvn help:describe -Dplugin=hr.yeti.rudimentary:rudimentary-maven-plugin:1.0-SNAPSHOT` command.

## Javadoc

Check [rudimentary-api](./rudimentary-api) module for Javadoc.

## Showcase module

Make sure to check [rudimentary-demo](./rudimentary-demo/src/main/java/hr/yeti/rudimentary/demo/endpoint) for code examples describing what is stated in **notable features** section.
Showcase can be run by using `mvn rudi:run` command (see how hello-world application is run) or by executing `run.sh` script found in the root of the *rudimentary-demo* module.
