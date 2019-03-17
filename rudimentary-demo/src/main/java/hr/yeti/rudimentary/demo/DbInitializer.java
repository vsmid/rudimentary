package hr.yeti.rudimentary.demo;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.sql.Sql;
import hr.yeti.rudimentary.sql.spi.JdbcConnectionPool;

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
    return new Class[]{ JdbcConnectionPool.class };
  }

}
