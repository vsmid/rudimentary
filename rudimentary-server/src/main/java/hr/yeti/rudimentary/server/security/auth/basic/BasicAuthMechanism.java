package hr.yeti.rudimentary.server.security.auth.basic;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.security.UsernamePasswordCredential;
import hr.yeti.rudimentary.security.spi.AuthMechanism;
import hr.yeti.rudimentary.security.spi.IdentityStore;
import java.util.Base64;

public final class BasicAuthMechanism extends AuthMechanism {

    private ConfigProperty enabled = new ConfigProperty("security.basic.enabled");

    private IdentityStore identityStore;

    @Override
    public Result doAuth(HttpExchange exchange) {
        Headers headers = exchange.getRequestHeaders();

        String auth = headers.getFirst("Authorization");

        if (auth == null) {
            Headers map = exchange.getResponseHeaders();
            return new Authenticator.Retry(401);
        }

        int spaceSeparatorIndex = auth.indexOf(' ');

        if (spaceSeparatorIndex == -1 || !auth.substring(0, spaceSeparatorIndex).equals("Basic")) {
            return new Authenticator.Failure(401);
        }

        byte[] credentials = Base64.getDecoder().decode(auth.substring(spaceSeparatorIndex + 1));
        String usernamePassword = new String(credentials);

        int colonIndex = usernamePassword.indexOf(':');

        String username = usernamePassword.substring(0, colonIndex);
        String password = usernamePassword.substring(colonIndex + 1);

        if (identityStore.validate(new UsernamePasswordCredential(username, password))) {
            return new Authenticator.Success(
                identityStore.getIdentity(
                    new HttpPrincipal(
                        username, realm.value()
                    )
                )
            );
        } else {
            Headers map = exchange.getResponseHeaders();
            return new Authenticator.Failure(401);
        }

    }

    @Override
    public boolean conditional() {
        return enabled.asBoolean();
    }

    @Override
    public Class[] dependsOn() {
        return new Class[]{ IdentityStore.class };
    }

    @Override
    public void initialize() {
        super.initialize();
        this.identityStore = Instance.of(IdentityStore.class);
    }

    @Override
    public void destroy() {

    }

}
