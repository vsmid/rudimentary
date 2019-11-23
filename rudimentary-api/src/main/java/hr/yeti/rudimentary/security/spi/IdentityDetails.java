package hr.yeti.rudimentary.security.spi;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.security.Identity;

/**
 * SPI which should retrieve user details in a customized way. Common usage would be as a part of {@link IdentityStore}.
 *
 * Since this interface extends {@link Instance} it means it is loaded automatically via {@link ServiceLoader} on application startup.
 *
 * There should be only one IdentityDetails provider per application.
 *
 * You can register it in
 * <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.security.spi.IdentityDetails</i>
 * file.
 *
 * @see rudimentary/rudimentary-exts/rudimentary-security-identitystore-embedded-ext module for the implementation of {@link IdentityStore} and the possible usage of IdentityDetails.
 *
 * @author vedransmid@yeti-it.hr
 * @param <D> Type of user details.
 */
public interface IdentityDetails<D> extends Instance {

    /**
     * Sets how user details are retrieved. The result should later be available via {@link Identity#getDetails()}.
     *
     * @param username Authenticated username.
     * @param realm Security realm name.
     * @return User details.
     */
    D details(String username, String realm);

}
