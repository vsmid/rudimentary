package hr.yeti.rudimentary.http.stream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Defines writing to output stream as a function.
 *
 * @author vedransmid@yeti-it.hr
 */
@FunctionalInterface
public interface StreamOutWriteDef {

  void startStreaming(OutputStream outputStream) throws IOException;
}
