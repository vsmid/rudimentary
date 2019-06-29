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
 * A very basic and limited implementation of SMTP protocol.
 *
 * <b>Important!</b>
 *
 * We should add a {@link Config} feature which would be able to load all properties into a single
 * object based on configuration properties prefix. This would further allow loading of all
 * javax.mail properties to enable all configuration properties without stating them in this class.
 *
 * <pre>
 * e.g. for the below configuration properties:
 *  email.properties.mail.smtp.auth
 *  email.properties.mail.smtp.starttls.enable
 *
 * a prefix <b>email.properties</b> would load the folowwing properties:
 *  mail.smtp.auth
 *  mail.smtp.starttls.enable
 * </pre>
 *
 * @see https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html
 *
 * @author vedransmid@gmail.com
 */
public class SmtpSessionPoolProvider extends EmailSessionPool {

  // Session pool settings
  private ConfigProperty minSize = new ConfigProperty("email.pool.minSize", "25");
  private ConfigProperty maxSize = new ConfigProperty("email.pool.maxSize", "50");
  private ConfigProperty validationInterval = new ConfigProperty("email.pool.validationInterval", "30");
  private ConfigProperty awaitTerminationInterval = new ConfigProperty("email.pool.awaitTerminationInterval", "10");

  // Email properties
  private ConfigProperty host = new ConfigProperty("email.host");
  private ConfigProperty port = new ConfigProperty("email.port");
  private ConfigProperty user = new ConfigProperty("email.user");
  private ConfigProperty password = new ConfigProperty("email.password");
  private ConfigProperty auth = new ConfigProperty("email.smtp.auth");
  private ConfigProperty enableStartTls = new ConfigProperty("email.smtp.starttls.enable");
  private ConfigProperty requiredStartTls = new ConfigProperty("email.smtp.starttls.required");

  private Authenticator authenticator;
  private Properties sessionProperties;

  @Override
  protected Session createObject() throws ObjectPoolException {
    return Session.getDefaultInstance(sessionProperties, authenticator);
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
    // This call is required, do not remove.
    super.initialize();

    // Set authenticator.
    authenticator = new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user.value(), password.value());
      }
    };

    // Very basic set of session properties.
    sessionProperties = new Properties();
    sessionProperties.put("mail.smtp.host", host.value());
    sessionProperties.put("mail.smtp.port", port.value());
    sessionProperties.put("mail.smtp.auth", auth.value());
    sessionProperties.put("mail.smtp.starttls.enable", enableStartTls.value());
    sessionProperties.put("mail.smtp.starttls.required", requiredStartTls.value());
  }

}
