module hr.yeti.rudimentary.server {
    requires jdk.httpserver;
    requires hr.yeti.rudimentary.api;
    requires java.logging;
    requires java.sql;
    requires java.net.http;

    exports hr.yeti.rudimentary.server;
    exports hr.yeti.rudimentary.server.test;    
    exports hr.yeti.rudimentary.server.http to hr.yeti.rudimentary.api;
    exports hr.yeti.rudimentary.server.http.processor to hr.yeti.rudimentary.api;
    exports hr.yeti.rudimentary.server.http.staticresources to hr.yeti.rudimentary.api;
    exports hr.yeti.rudimentary.server.jdbc to hr.yeti.rudimentary.api;
    exports hr.yeti.rudimentary.server.security.csrf to hr.yeti.rudimentary.api;
    exports hr.yeti.rudimentary.server.security.cors to hr.yeti.rudimentary.api;
    exports hr.yeti.rudimentary.server.security.identitystore.embedded to hr.yeti.rudimentary.api;
    exports hr.yeti.rudimentary.server.security.auth.basic to hr.yeti.rudimentary.api;
    exports hr.yeti.rudimentary.server.security.auth.loginform to hr.yeti.rudimentary.api;
    exports hr.yeti.rudimentary.server.http.session to hr.yeti.rudimentary.api;
    exports hr.yeti.rudimentary.server.http.session.listener to hr.yeti.rudimentary.api;
    exports hr.yeti.rudimentary.server.mvc to hr.yeti.rudimentary.api;
    exports hr.yeti.rudimentary.server.email.smtp to hr.yeti.rudimentary.api;
    exports hr.yeti.rudimentary.server.healthcheck to hr.yeti.rudimentary.api;
    exports hr.yeti.rudimentary.server.apidocs to hr.yeti.rudimentary.api; 
    exports hr.yeti.rudimentary.server.config to hr.yeti.rudimentary.api;
    
    uses hr.yeti.rudimentary.http.spi.HttpEndpoint;
    uses hr.yeti.rudimentary.config.spi.Config;
    uses hr.yeti.rudimentary.context.spi.Context;
    uses hr.yeti.rudimentary.mvc.spi.ViewEngine;
}
