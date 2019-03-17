package hr.yeti.rudimentary.security;

public class UsernamePasswordCredential implements Credential {

  private String username;
  private String password;

  public UsernamePasswordCredential(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

}
