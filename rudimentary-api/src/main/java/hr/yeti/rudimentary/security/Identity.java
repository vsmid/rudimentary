package hr.yeti.rudimentary.security;

import com.sun.net.httpserver.HttpPrincipal;
import java.util.Collections;
import java.util.List;

public class Identity<I> extends HttpPrincipal {

    private String password;
    private List<String> groups;
    private List<String> roles;
    private I details;

    public Identity(Identity identity, I details) {
        super(identity.getUsername(), identity.getPassword());
        this.groups = Collections.unmodifiableList(identity.getGroups());
        this.roles = Collections.unmodifiableList(identity.getRoles());
        this.details = details;
    }

    public Identity(String username, String realm) {
        super(username, realm);
        this.groups = List.of();
        this.roles = List.of();
    }

    public Identity(List<String> groups, List<String> roles, I details, String username, String password, String realm) {
        super(username, realm);
        this.password = password;
        this.groups = Collections.unmodifiableList(groups);
        this.roles = Collections.unmodifiableList(roles);
        this.details = details;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getGroups() {
        return groups;
    }

    public List<String> getRoles() {
        return roles;
    }

    public I getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "Identity{username=" + getUsername() + ", realm=" + getRealm() + ", groups=" + groups + ", roles=" + roles + ", details=" + details + '}';
    }
}
