package hr.yeti.rudimentary.http.stream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Define writing response to output stream as a function.
 *
 * @author vedransmid@yeti-it.hr
 */
@FunctionalInterface
public interface StreamOutDef {

  void startStreaming(OutputStream outputStream) throws IOException;
}
