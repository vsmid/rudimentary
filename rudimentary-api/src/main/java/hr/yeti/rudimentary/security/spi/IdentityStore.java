package hr.yeti.rudimentary.security.spi;

import com.sun.net.httpserver.HttpPrincipal;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.security.Credential;
import hr.yeti.rudimentary.security.Identity;
import hr.yeti.rudimentary.security.UsernamePasswordCredential;

/**
 * SPI used to validate incoming user credentials and retrieving basic user information.
 *
 * Since this interface extends {@link Instance} it means it is loaded automatically via
 * {@link ServiceLoader} on application startup.
 *
 * There should be only one IdentityStore provider per application.
 *
 * You can register it in
 * <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.security.spi.IdentityStore</i>
 * file.
 *
 * @see rudimentary/rudimentary-exts/rudimentary-security-identitystore-embedded-ext module for the
 * implementation of {@link IdentityStore}.
 *
 * @author vedransmid@yeti-it.hr.
 */
public interface IdentityStore extends Instance {

  /**
   * Check incoming user credentials against resource holding user information such as database,
   * LDAP, memory, file etc.
   *
   * @param credential A type of credentials used, for now there is only
   * {@link UsernamePasswordCredential} which provides username and password.
   * @return true if user is valid, otherwise false.
   */
  boolean validate(Credential credential);

  /**
   * Retrieve user info based on the {@link HttpPrincipal#getUsername()} and/or
   * {@link HttpPrincipal#getRealm()}.
   *
   * @param principal A user for which identity is to be retrieved.
   * @return User identity info in the form of {@link Identity}.
   */
  Identity getIdentity(HttpPrincipal principal);

}
