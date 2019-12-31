# Health Check

## Introduction
In order to see the status of given application/service Rudimentary offers a way to implement any kind of a report you need.

## Adding new health check report
To add new health check report just implement `hr.yeti.rudimentary.health.spi.HealthCheck` interface. You can register this service provider in a `src/main/resources/META-INF/services/hr.yeti.rudimentary.health.spi.HealthCheck` file. This however, rudimentary-maven-plugin automatically does for you.

## Default health check
By deafult, if no health check providers are found, only application/service memory status will be reported.

## /_health http endpoint
To get health check report, issue http `GET` reuqest to `/_health` uri.
