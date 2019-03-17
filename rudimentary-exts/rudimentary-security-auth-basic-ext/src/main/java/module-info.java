module hr.yeti.rudimentary.ext.security.basic {
  requires hr.yeti.rudimentary.api;

  exports hr.yeti.rudimentary.ext.security.basic;

  provides hr.yeti.rudimentary.security.spi.AuthMechanism with hr.yeti.rudimentary.ext.security.basic.BasicAuthMechanism;
}
