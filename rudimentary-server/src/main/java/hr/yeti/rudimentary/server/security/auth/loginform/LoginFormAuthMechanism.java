package hr.yeti.rudimentary.server.security.auth.loginform;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.HttpRequestUtils;
import hr.yeti.rudimentary.http.content.Form;
import hr.yeti.rudimentary.security.UsernamePasswordCredential;
import hr.yeti.rudimentary.security.spi.AuthMechanism;
import hr.yeti.rudimentary.security.spi.IdentityStore;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class LoginFormAuthMechanism extends AuthMechanism {

    private ConfigProperty enabled = new ConfigProperty("security.loginForm.enabled");
    private ConfigProperty loginURI = new ConfigProperty("security.loginForm.loginURI");
    private ConfigProperty redirectAfterSuccessfulLoginURI = new ConfigProperty("security.loginForm.redirectAfterSuccessfulLoginURI");
    private ConfigProperty usernameFieldName = new ConfigProperty("security.loginForm.usernameFieldName");
    private ConfigProperty passwordFieldName = new ConfigProperty("security.loginForm.passwordFieldName");

    private IdentityStore identityStore;

    @Override
    public boolean conditional() {
        return enabled.asBoolean();
    }

    @Override
    public boolean startAuthenticatedSessionOnSuccessfulAuth() {
        return true;
    }

    @Override
    public Result doAuth(HttpExchange exchange) {
        Map<String, Object> formData = null;

        try {
            formData = HttpRequestUtils.parseQueryParameters(
                new String(exchange.getRequestBody().readAllBytes())
            );
        } catch (IOException ex) {
            Logger.getLogger(LoginFormAuthMechanism.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (formData.isEmpty()) {
            Headers map = exchange.getResponseHeaders();
            map.set("Location", loginURI.value());
            return new Authenticator.Retry(302);
        }

        Form form = new Form(formData);

        String username = form.getValue().getOrDefault(usernameFieldName.value(), "").toString();
        String password = form.getValue().getOrDefault(passwordFieldName.value(), "").toString();

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
            map.set("Location", loginURI.value());
            return new Authenticator.Failure(401);
        }

    }

    @Override
    public Class[] dependsOn() {
        return new Class[]{ IdentityStore.class };
    }

    @Override
    public void initialize() {
        super.initialize();
        // Flag redirecting URI to require authentication so authentication mechanism is triggered.
        super.urisRequiringAuthenticationCache.add(Pattern.compile(redirectAfterSuccessfulLoginURI.value()));
        this.identityStore = Instance.of(IdentityStore.class);
    }

    @Override
    public void destroy() {

    }
}
