package hr.yeti.rudimentary.server.resources;

import java.io.InputStream;
import java.util.Objects;

public class ClasspathResource {

    private String path;

    public ClasspathResource(String path) {
        Objects.requireNonNull(path);

        this.path = !path.startsWith("/") ? "/" + path : path;
    }

    public InputStream get() {
        return this.getClass().
                getResourceAsStream(path);
    }

}
