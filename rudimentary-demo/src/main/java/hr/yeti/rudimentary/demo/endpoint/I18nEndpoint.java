package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Json;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.i18n.I18n;
import java.util.Locale;
import java.util.Map;

public class I18nEndpoint implements HttpEndpoint<Empty, Json> {

    @Override
    public String path() {
        return "i18n";
    }

    @Override
    public Json response(Request<Empty> request) {
        return new Json(
            Map.of(
                "en", I18n.text("message", 20),
                "hr", I18n.text("message", Locale.forLanguageTag("hr"), 20),
                "en_no_key_default_msg_returned", I18n.text("msg", "{0} is not that old.", 20)
            )
        );
    }

    @Override
    public String description() {
        return "Show usage of i18n.";
    }

}
