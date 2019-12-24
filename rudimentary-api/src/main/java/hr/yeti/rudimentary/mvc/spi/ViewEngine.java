package hr.yeti.rudimentary.mvc.spi;

import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.mvc.ViewEngineException;
import java.util.ServiceLoader;

/**
 * SPI in charge of processing MVC. It is mandatory to have ViewEngine registered in order for {@link ViewEngine} to
 * work properly. Providers of this SPI transform dynamic HTML templates to a raw string representing HTML to be sent
 * out in a HTTP response.
 *
 * Since this interface extends {@link Instance} it means it is loaded automatically via {@link ServiceLoader} on
 * application startup.
 *
 * You can have only one ViewEngine provider registered per application and you can register it in
 * <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.mvc.spi.ViewEngine</i>
 * file.
 *
 * @author vedransmid@yeti-it.hr
 */
public interface ViewEngine extends Instance {

    /**
     * A core method of the MVC view engine. This is the method that will call the underlying template engine's method
     * which dynamically applies context values to a template file and produces a HTML string to be sent out in a HTTP
     * response.
     *
     * Some of the template engines available are: Pebble, Jade, Apache Velocity, Apache FreeMarker, Thymeleaf,
     * Mustache, Handlebars etc.
     *
     * This framework comes with out-of-the box implementation of the Pebble templating engine which you can use as an
     * example if you wish to add your own ViewEngine provider. It can be found in
     * <b>rudimentary-exts/rudimentary-mvc-pebble-ext</b> module.
     *
     * @param view An object returned by executing {@link ViewEndpoint#response(hr.yeti.rudimentary.http.Request)}.
     * @return HTML string to be rendered on the client(e.g. browser).
     * @throws ViewEngineException
     */
    String render(View view) throws ViewEngineException;

    /**
     * Set the location of the template directory. Default value is provided via {@link Config} property:
     * {@code mvc.templatesDir=view}
     *
     * @return A root directory where template files can be found. Usually under src/main/resources directory.
     */
    String getTemplatesDirectory();

    /**
     * Set the location of the static resources (css, javascript, images, etc.) directory. Default value is provided via {@link Config} property: {@code mvc.staticResourcesDir=static}
     *
     * @return A root directory where static resources files can be found. Usually under
     * src/main/resources directory.
     */
    String getStaticResourcesDirectory();

}
