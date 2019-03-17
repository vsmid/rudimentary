module hr.yeti.rudimentary.api {
  exports hr.yeti.rudimentary.config;
  exports hr.yeti.rudimentary.config.spi;

  exports hr.yeti.rudimentary.context.spi;

  exports hr.yeti.rudimentary.email;
  exports hr.yeti.rudimentary.email.spi;

  exports hr.yeti.rudimentary.exception;
  exports hr.yeti.rudimentary.exception.spi;

  exports hr.yeti.rudimentary.http;
  exports hr.yeti.rudimentary.http.spi;
  exports hr.yeti.rudimentary.http.content;

  exports hr.yeti.rudimentary.health;
  exports hr.yeti.rudimentary.health.spi;

  exports hr.yeti.rudimentary.mvc;
  exports hr.yeti.rudimentary.mvc.spi;

  exports hr.yeti.rudimentary.interceptor.spi;

  exports hr.yeti.rudimentary.pooling;
  exports hr.yeti.rudimentary.pooling.spi;

  exports hr.yeti.rudimentary.security;
  exports hr.yeti.rudimentary.security.spi;

  exports hr.yeti.rudimentary.session;

  exports hr.yeti.rudimentary.sql;
  exports hr.yeti.rudimentary.sql.spi;

  exports hr.yeti.rudimentary.validation;

  requires transitive java.json.bind;
  requires transitive java.json;

  requires transitive jdk.httpserver; // Maybe this should be removed, not worth having just because of HttpHeaders

  requires transitive java.sql;

  requires transitive javax.mail.api;
}
