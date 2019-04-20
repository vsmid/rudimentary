package hr.yeti.rudimentary.demo;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.sql.Sql;
import java.io.InputStream;
import java.util.Scanner;
import javax.sql.DataSource;

public class DbInitializer implements Instance {

  @Override
  public void initialize() {
    Sql.tx((sql) -> {
      sql.update("DROP TABLE IF EXISTS USERS;");
      sql.update("CREATE TABLE USERS(ID, NAME);");
      return null;
    });
  }

  @Override
  public Class[] dependsOn() {
    return new Class[]{ DataSource.class };
  }

  /**
   * Loads script from classpath.
   *
   * @param script
   */
  private void loadScript(InputStream script) {
    Scanner scanner = new Scanner(script);
    scanner.useDelimiter("(;(\r)?\n)|(--\n)");

    Sql.tx((sql) -> {
      while (scanner.hasNext()) {
        String line = scanner.next();
        if (line.startsWith("/*!") && line.endsWith("*/")) {
          int i = line.indexOf(' ');
          line = line.substring(i + 1, line.length() - " */".length());
        }
        if (line.trim().length() > 0) {
          if (!line.endsWith(";")) {
            line += ";";
          }
          sql.update(line);
        }
      }
      return null;
    });
  }
}
