package hr.yeti.rudimentary.demo.endpoint;

import com.sun.net.httpserver.Headers;
import hr.yeti.rudimentary.http.MediaType;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.ByteStream;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;
import java.util.Arrays;

public class StreamOutEndpoint implements HttpEndpoint<Empty, ByteStream> {

    @Override
    public URI path() {
        return URI.create("/streamOut");
    }

    @Override
    public ByteStream response(Request<Empty> request) {
        return new ByteStream((outputStream) -> {
            for (int i = 0; i < 100000; i++) {
                outputStream.write((String.valueOf(i) + System.lineSeparator()).getBytes());
                // Flush after each write.
                outputStream.flush();
            }
        });
    }

    @Override
    public Headers responseHttpHeaders(Headers headers) {
        // To use stream for download a document, set below http headers. Do not override this method
        // if you want plain writing to stream.
        Headers responseHeaders = new Headers();
        responseHeaders.put("Content-Type", Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
        responseHeaders.put("Content-Disposition", Arrays.asList("attachment;filename=streamOut.txt"));
        return responseHeaders;
    }

    @Override
    public String description() {
        return "Stream data out.";
    }

}
