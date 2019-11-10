import hr.yeti.rudimentary.http.spi.HttpEndpoint;

module hr.yeti.rudimentary.demo {
  requires hr.yeti.rudimentary.api;
  requires hr.yeti.rudimentary.server;
  
  requires jakarta.mail;

  uses HttpEndpoint;
}
