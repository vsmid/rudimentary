module hr.yeti.rudimentary.exts.mvc.pebble {
  requires hr.yeti.rudimentary.api;

  exports hr.yeti.rudimentary.exts.mvc.pebble;

  requires pebble;
  requires unbescape;
  requires slf4j.api;

  requires java.logging;
  
  provides hr.yeti.rudimentary.mvc.spi.ViewEngine with hr.yeti.rudimentary.exts.mvc.pebble.PebbleViewEngine;
}
