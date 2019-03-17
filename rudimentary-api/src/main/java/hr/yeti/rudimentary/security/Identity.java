package hr.yeti.rudimentary.security;

import com.sun.net.httpserver.HttpPrincipal;
import java.util.List;

public class Identity<I> extends HttpPrincipal {

  private String password;
  private List<String> groups;
  private List<String> roles;
  private I details;

  public Identity(Identity identity, I details) {
    super(identity.getUsername(), identity.getPassword());
    this.groups = identity.getGroups();
    this.roles = identity.getRoles();
    this.details = details;
  }

  public Identity(String username, String realm) {
    super(username, realm);
  }

  public Identity(List<String> groups, List<String> roles, I details, String username, String password, String realm) {
    super(username, realm);
    this.password = password;
    this.groups = groups;
    this.roles = roles;
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
