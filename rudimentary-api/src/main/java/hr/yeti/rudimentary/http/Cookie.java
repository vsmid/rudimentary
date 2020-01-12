package hr.yeti.rudimentary.http;

import java.net.HttpCookie;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Cookie {

    private HttpCookie httpCookie;
    private boolean sameSiteStrict;

    public Cookie(HttpCookie httpCookie) {
        this.httpCookie = httpCookie;
    }

    @Override
    public String toString() {
        StringBuilder cookieString = new StringBuilder();

        cookieString
            .append(pair(httpCookie.getName(), httpCookie.getValue()))
            .append(pair("Path", httpCookie.getPath()))
            .append(pair("Domain", httpCookie.getDomain()))
            .append(pair("SameSite", sameSiteStrict ? "strict" : "lax"));

        if (httpCookie.getMaxAge() > -1) {
            OffsetDateTime expires = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofSeconds(httpCookie.getMaxAge()));
            cookieString.append(
                pair("Expires", DateTimeFormatter.RFC_1123_DATE_TIME.format(expires))
            );
        }

        if (httpCookie.isHttpOnly()) {
            cookieString.append("HttpOnly;");
        }

        if (httpCookie.getSecure()) {
            cookieString.append("Secured;");
        }

        return cookieString.toString();
    }

    private String pair(String name, Object value) {
        return String.format("%s=%s;", name, Objects.isNull(value) ? "" : value.toString());
    }

    public void setSameSiteStrict(boolean strict) {
        this.sameSiteStrict = strict;
    }

}
