import hr.yeti.rudimentary.ext.apidocs.ApiDocsEndpoint;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;

module hr.yeti.rudimentary.ext.apidocs {
  requires hr.yeti.rudimentary.api;

  provides HttpEndpoint with ApiDocsEndpoint;
}
