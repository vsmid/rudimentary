package hr.yeti.rudimentary.server.email.smtp;

import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.email.spi.EmailSessionPool;
import hr.yeti.rudimentary.pooling.ObjectPoolException;
import hr.yeti.rudimentary.pooling.ObjectPoolSettings;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

/**
 * Implementation of SMTP session pool provider.
 *
 * @see https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html
 *
 * @author vedransmid@gmail.com
 */
public class SmtpSessionPoolProvider extends EmailSessionPool {

    // Session pool settings
    private ConfigProperty minSize = new ConfigProperty("email.smtp.pool.minSize");
    private ConfigProperty maxSize = new ConfigProperty("email.smtp.pool.maxSize");
    private ConfigProperty validationInterval = new ConfigProperty("email.smtp.pool.validationInterval");
    private ConfigProperty awaitTerminationInterval = new ConfigProperty("email.smtp.pool.awaitTerminationInterval");

    private ConfigProperty user = new ConfigProperty("email.smtp.user");
    private ConfigProperty password = new ConfigProperty("email.smtp.password");

    private Properties smtpSessionProperties = Config.provider().getPropertiesByPrefix("email.smtp.properties", false);

    private Authenticator authenticator;

    @Override
    protected Session createObject() throws ObjectPoolException {
        if (useAuthentication()) {
            return Session.getInstance(smtpSessionProperties, authenticator);
        } else {
            return Session.getInstance(smtpSessionProperties);
        }
    }

    @Override
    protected ObjectPoolSettings settings() {
        return new ObjectPoolSettings(
            minSize.asInt(),
            maxSize.asInt(),
            validationInterval.asInt(),
            awaitTerminationInterval.asInt()
        );
    }

    @Override
    public void initialize() {
        // Set authenticator.
        if (useAuthentication()) {
            authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        user.value(),
                        password.value()
                    );
                }
            };
        }

        // This call is required, do not remove.
        super.initialize();
    }

    @Override
    public boolean conditional() {
        return Config.provider().property("email.smtp.enabled").asBoolean();
    }

    public boolean useAuthentication() {
        return !user.value().isBlank() && !password.value().isBlank();
    }
}
