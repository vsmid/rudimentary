package hr.yeti.rudimentary.email;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.email.spi.EmailSessionPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

/**
 * Class used for static access to email communication. Main goal of this class to reduce boilerplate code as much as possible and just write email messages on the fly.
 *
 * @author vedransmid@yeti-it.hr
 */
public class Email {

    private Session session;

    private Email() {
        // TODO Throw exception explaining how email pool is not configured if session == null
        this.session = Instance.of(EmailSessionPool.class).borrow();
    }

    /**
     * Sends given email message.
     *
     * @param message An email message in the form of {@link EmailMessage}.
     */
    public static void send(EmailMessage message) {
        Email email = new Email();

        try {
            MimeMessage mimeMessage = new MimeMessage(email.getSession());
            message.compose(mimeMessage);

            Transport.send((Message) mimeMessage);
        } catch (MessagingException ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
            throw new EmailException(ex.getMessage(), ex);
        }
    }

    public Session getSession() {
        return session;
    }

}
