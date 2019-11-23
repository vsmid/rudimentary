package hr.yeti.rudimentary.email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Define email as a function. This comes in handy when you want to keep your email message templates in a separate class.
 *
 * e.g.
 *
 * <pre>
 * {@code
 * public class EMAILS {
 *
 *  public static EmailMessage bigfoot(String text) {
 *    return (message) -> {
 *      message.setFrom("yeti@yeti-it.hr");
 *      message.setRecipients(Message.RecipientType.TO, "bigfoot@yeti-it.hr");
 *      message.setSubject("A message");
 *      message.setText(text);
 *    };
 *  }
 *
 * }
 * ...
 *
 * Email.send(EMAILS.bigfoot("Boohoo!"));
 *}
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
@FunctionalInterface
public interface EmailMessage {

    void compose(MimeMessage message) throws MessagingException;
}
