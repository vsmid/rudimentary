module hr.yeti.rudimentary.exts.http.xml {
    requires hr.yeti.rudimentary.api;
    requires transitive java.xml;
    requires transitive java.xml.bind;

    exports hr.yeti.rudimentary.exts.http.xml;

    uses hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;

    provides hr.yeti.rudimentary.http.content.handler.spi.ContentHandler with hr.yeti.rudimentary.exts.http.xml.XmlPojoContentHandler;
    
    opens hr.yeti.rudimentary.exts.http.xml to java.xml.bind;
}
