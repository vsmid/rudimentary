module hr.yeti.rudimentary.server {
  requires jdk.httpserver;
  requires hr.yeti.rudimentary.api;
  requires java.logging;
  requires java.sql;
  requires com.zaxxer.hikari;

  exports hr.yeti.rudimentary.server;

  uses hr.yeti.rudimentary.http.spi.HttpEndpoint;
  uses hr.yeti.rudimentary.config.spi.Config;
  uses hr.yeti.rudimentary.context.spi.Context;
}
