# Rudimentary framework

## Introduction
A small an simple flat web services/applications framework. 
Flat means no strict separation on controller, service and repository layers.
One web method implementation is contained within a single Java file.

## Roadmap

* Add MVC caching policy settings
* Add MVC escaping strategies, e.g. html, js, css, attributes, read OWASP
* Add Security LoginForm authentication option
* Add Security JWT authentication option - jose4j
* Add Security CSRF protection
* Add Sql rollback options
* Add HttpEndpoint async() method for async execution
* Add Metrics (Time, Gauge, Count)
* Add ApiDoc - read OpenApi
* Add Tracing - read OpenTracing
* Finish tests and README docs