module hr.yeti.rudimentary.ext.security.identitystore.embedded {
  exports hr.yeti.rudimentary.ext.security.identitystore.embedded;

  requires hr.yeti.rudimentary.api;

  provides hr.yeti.rudimentary.security.spi.IdentityStore with hr.yeti.rudimentary.ext.security.identitystore.embedded.EmbeddedIdentityStore;
}
