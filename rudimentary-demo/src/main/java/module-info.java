
import hr.yeti.rudimentary.http.spi.HttpEndpoint;

module hr.yeti.rudimentary.demo {
    requires hr.yeti.rudimentary.api;
    requires hr.yeti.rudimentary.server;
    requires transitive hr.yeti.rudimentary.exts.http.xml;
    requires java.xml.bind;
   
    requires jakarta.mail;

    
    uses HttpEndpoint;
}
