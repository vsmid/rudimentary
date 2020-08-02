package hr.yeti.rudimentary.server.security.identitystore.embedded;

import com.sun.net.httpserver.HttpPrincipal;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.security.Credential;
import hr.yeti.rudimentary.security.Identity;
import hr.yeti.rudimentary.security.IdentityStoreException;
import hr.yeti.rudimentary.security.UsernamePasswordCredential;
import hr.yeti.rudimentary.security.spi.IdentityStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import hr.yeti.rudimentary.security.spi.IdentityDetails;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmbeddedIdentityStore implements IdentityStore {

    private ConfigProperty identities = new ConfigProperty("security.identityStore.embedded.identities");
    private ConfigProperty realm = new ConfigProperty("security.realm");

    private IdentityDetails identityDetails;

    private ConcurrentHashMap<String, Identity> identitiesMap = new ConcurrentHashMap<>();

    @Override
    public boolean validate(Credential credential) {
        // For now only UernamePasswordCredential is supported.
        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential usernamePassword = (UsernamePasswordCredential) credential;

            Identity identity = identitiesMap.get(usernamePassword.getUsername());

            if (Objects.nonNull(identity)
                && Objects.nonNull(identity.getPassword())
                && identity.getPassword().equals(usernamePassword.getPassword())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Identity<?> getIdentity(HttpPrincipal principal) {
        Identity identity = identitiesMap.get(principal.getUsername());
        return identity;
    }

    @Override
    public Class[] dependsOn() {
        return new Class[]{ IdentityDetails.class };
    }

    @Override
    public void initialize() {
        // Allow loading pf users via file as another option.
        String[] identitiesArray = identities.value().split(";");

        for (String identityDefinition : identitiesArray) {
            String[] values = identityDefinition.split(":");

            // Format is username:password:groups:roles:details
            // Only details can be left out completely..
            // e.g. vsmid:pass:admins::
            // e.g. vsmid:pass::read,write:
            // e.g. vsmid:pass::read,write:email=vsmid@gmail.com,city=Zagreb
            if (values.length < 4) {
                throw new IdentityStoreException("Invalid embedded identity definition format found: " + identityDefinition);
            }

            String username = values[0].trim();
            String password = values[1].trim();
            List<String> groups = Stream.of(values[2].split(","))
                .map(String::trim)
                .collect(Collectors.toList());
            List<String> roles = Stream.of(values[3].split(","))
                .map(String::trim)
                .collect(Collectors.toList());
            String details = values.length == 5 ? values[4] : null;

            Map<String, String> detailsMap = null;
            if (Objects.nonNull(details)) {
                detailsMap = new HashMap<>();
                String[] detailProperties = details.split(",");
                for (String property : detailProperties) {
                    String[] keyVal = property.split("=");
                    detailsMap.put(keyVal[0], keyVal[1]);
                }

            }
            identitiesMap.put(username, new Identity(
                groups,
                roles,
                Objects.nonNull(detailsMap) ? detailsMap : null,
                username,
                password,
                realm.value()
            ));
        }

    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean conditional() {
        return realm.value().equals("embedded");
    }

}
