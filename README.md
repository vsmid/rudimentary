# Rudimentary framework

A small and simple flat web services/applications framework. 

## Introduction

It all started as a playground for the new Java module system.

The goal of this framework it to produce the simplest possible Java framework by trying to use only JDK (notable exceptions are libraries dealing with JSON conversions). It avoides using annotations and reflection as much as possible.

This framework is also opinionated and promotes flat application/service design. This means there is no strict separation on controller, service and repository layers. One web method implementation is contained within a single Java file. Of course, there is nothing preventing you to do it your own way :)

This framework also promotes static access to various resources such as sql, email and object instances by using Java's ServiceLoader utility to reduce boilerplate code as much as possible. The goal is to not have object instances as properties in classes. If you want to use something, it should be available in a static way.


#### Notable features

* Easy and simple configuration via properties file, environment and system properties
* Simple dependency injection mechanism
* Simple and efficient validation pattern
* Simple SQL access
* Simple email access
* Simple MVC framework
* Simple application/service security configuration
* It is easy to extends framework with your own custom stuff (look at the exts module)

Majority of features are a work in progress but are usable and show the intent.

## Roadmap

* Add MVC caching policy settings
* Add MVC escaping strategies, e.g. html, js, css, attributes, read OWASP
* Add Security LoginForm authentication option
* Add Security JWT authentication option - jose4j
* Add Security CSRF protection
* Add Sql rollback options
* Add HttpEndpoint async() method for async execution
* Add Metrics (Time, Gauge, Count)
* Add ApiDoc - read OpenApi - added very basic opinionated extension
* Add Tracing - read OpenTracing
* Finish tests and README docs
* Add environment to Instance and implement loading based on environment variable in configuration
* Create test utilities (see Context tests for creating mock context)
* Add Array as new content type - Json content type can currently handle it but this will some refactor to be more efficient. Consider giving Json inferred type?
* Add Admin module