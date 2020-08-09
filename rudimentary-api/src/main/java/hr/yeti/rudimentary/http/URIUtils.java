package hr.yeti.rudimentary.http;

import java.net.URI;
import java.util.regex.Pattern;

public class URIUtils {

    public static final String PATH_VAR_STRING = ":[a-zA-Z0-9]+";
    public static final Pattern PATH_VAR_PATTERN = Pattern.compile("(" + PATH_VAR_STRING + ")");

    public static Pattern convertToRegex(String uri, String expression) {
        return Pattern.compile("^" + uri.replaceAll(PATH_VAR_STRING, expression) + "$");
    }

    public static URI removeSlashPrefix(URI uri) {
        String uriString = uri.getPath();
        return removeSlashPrefix(uriString);
    }
    
    public static URI removeSlashPrefix(String uri) {
        if (uri.startsWith("/")) {
            return URI.create(uri.substring(1));
        } else {
            return URI.create(uri);
        }
    }

    public static URI prependSlash(URI uri) {
        String uriString = uri.getPath();
        return prependSlash(uriString);
    }

    public static URI prependSlash(String uri) {
        if (!uri.startsWith("/")) {
            return URI.create("/" + uri);
        } else {
            return URI.create(uri);
        }
    }

}
