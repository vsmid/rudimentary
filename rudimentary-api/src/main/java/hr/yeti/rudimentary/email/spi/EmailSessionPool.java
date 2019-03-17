package hr.yeti.rudimentary.email.spi;

import hr.yeti.rudimentary.email.Email;
import hr.yeti.rudimentary.pooling.spi.ObjectPool;
import javax.mail.Session;

/**
 * Specialized {@link ObjectPool} SPI for providing Email {@link Session} pool.
 *
 * Since this abstract class inherently extends {@link Instance} it means it is loaded automatically
 * via {@link ServiceLoader} on application startup.
 *
 * You can have as many different EmailSessionPool providers as you want and you can register them
 * in
 * <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.email.spi.EmailSessionPool</i>
 * file. Currently however, the one marked as priority one or the first one listed will used.
 *
 * By extending this class you basically provide a way to send emails. An example of one such
 * implementation can be seen in
 * <b>rudimentary/rudimentary-exts/rudimentary-email-smtp-ext</b> module.
 *
 * This is then directly used by the {@link Email} to handle emails.
 *
 * @author vedransmid@yeti-it.hr
 */
public abstract class EmailSessionPool extends ObjectPool<Session> {

}
