# Sql
One of the most commonly used things in almost any application/service is database. Rudimentary offers a really nice and simple way of communicating with the database.

## Default datasource
Rudimentray uses [HikariCP](https://github.com/brettwooldridge/HikariCP). Implementation of Rudimentary basic datasource using `HikariCP` can be seen in `hr.yeti.rudimentary.sql.spi.BasicDataSource` class. 

## Configuring single datasource - classic DriverManager approach
Configuration is done via configuration properties.
```properties
# JDBC Sqlite datasource configuration example
dataSource.enabled=true
dataSource.maximumPoolSize=25
dataSource.driverClassName=org.sqlite.JDBC
dataSource.jdbcUrl=jdbc:sqlite:file::memory:?cache=shared
dataSource.username=
dataSource.password=
```
## Configuring multiple datasources - classic DriverManager approach
Sometimes application/service can communicate with multiple databases. If that is your case, you can configure multiple data sources like this:
```properties
# default datasource
dataSource.enabled=true
dataSource.maximumPoolSize=25
dataSource.driverClassName=org.sqlite.JDBC
dataSource.jdbcUrl=jdbc:sqlite:file::memory:?cache=shared
dataSource.username=
dataSource.password=

# otherDs datasource
dataSource.otherDs.enabled=true
dataSource.otherDs.maximumPoolSize=25
dataSource.otherDs.driverClassName=org.sqlite.JDBC
dataSource.otherDs.jdbcUrl=jdbc:sqlite:/otherDb.sql?cache=shared
dataSource.otherDs.username=
dataSource.otherDs.password=
```
## Configuring datasource properties
To add dataSource properties just use `dataSource.properties.*` notation. Take a look at [HikariCP initialization section](https://github.com/brettwooldridge/HikariCP) for more details on configuration options. You can also use this approach to fully configure dataSource without using classic DriverManager approach.
```properties
# default datasource
dataSource.enabled=true
dataSource.maximumPoolSize=25
dataSource.properties.dataSourceClassName=org.postgresql.ds.PGSimpleDataSource
dataSource.properties.user=test
dataSource.properties.password=test
dataSource.properties.databaseName=mydb
dataSource.properties.portNumber=5432
dataSource.properties.serverName=localhost

# otherDs datasource
dataSource.otherDs.enabled=true
dataSource.otherDs.maximumPoolSize=25
dataSource.otherDs.properties.dataSourceClassName=org.postgresql.ds.PGSimpleDataSource
dataSource.otherDs.properties.user=test
dataSource.otherDs.properties.password=test
dataSource.otherDs.properties.databaseName=mydb2
dataSource.otherDs.properties.portNumber=5432
dataSource.otherDs.properties.serverName=localhost
```

To see how you can actually query database using another datasource, see *Query using specific datasource* section.

The thing to remember is that `otherDs` in `dataSource.otherDs.*` properties should match `Instance#id` of the class which extends `hr.yeti.rudimentary.sql.spi.BasicDataSource` class. 

To create another datasource just extend `hr.yeti.rudimentary.sql.spi.BasicDataSource` class. You can have an many datasources as you like and you can register them in
 `src/main/resources/META-INF/services/hr.yeti.rudimentary.sql.spi.BasicDataSource` file. 
This however, `rudimentary-maven-plugin` already automatically does for you.
```java
package hr.yeti;

public class OtherDs extends BasicDataSource {

  @Override
  public String id() {
      return "otherDs";
  }

}
```
## Query for single result
```java
Sql.query().row("select * from users where id=?;", 1);
```
## Query for multiple results
```java
Sql.query().rows("select * from users;");
```
## Insert, update, delete queries
```java
Sql.query().update("insert into users(id, name) values(1, 'M');");
Sql.query().update("update users set name='Lena' where id=1;");
Sql.query().update("delete from users where id=1;);
```
## Query using specific datasource
By default, `Sql#query` method uses default datasource. To use different datasource, just set `Sql#query` method's `dataSourceId` parameter (check *Configuring multiple datasources* section to see how `otherDs` is configured). 
```java
Sql.query("otherDs").rows("select * from users;");
```
## Query using repository
Sometimes you wish to reuse Sql queries or you just want to make your code a little bit cleaner if you have a lot of queries. Here is a really simple way of how you can group Sql queries in a repository.

**USER repository class containing Sql queries**
```java
public class USER {
  static SqlQueryDef<Map<String, Object>> getById(long id) {
    return (sql) -> {
        return sql.row("select * from users where id=?;", id);
    };
  }
}
```
**Using USER repository**
```java
...
Sql.query(USER.getById(1));
```
## Transactions
```java
Sql.tx((sql) -> {
  sql.update("insert into users(id, name) values(1, 'M');");
  sql.update("insert into users(id, name) values(2, 'M');");
  sql.update("insert into users(id, name) values(3, 'M');");
  return sql.rows("select * from users;");
});
```
## Transaction rollback
You can define exceptions for which transaction will or will not be rolled back by setting `Sql#tx` method's `rollbackOn` and `noRollbackOn` parameters. Exceptions defined in `noRollbackOn` parameter have greater priority than the ones defined in `rollbackOn` parameter.

Below example shows how to perform rollback for all exceptions of type `java.lang.Exception` except  `java.io.FileNotFoundException`.
```java
Sql.tx(
  (sql) -> {
      sql.update("insert into users(id, name) values(1, 'M');");
      sql.update("insert into users(id, name) values(2, 'M');");
      sql.update("insert into users(id, name) values(3, 'M');");
      return sql.rows("select * from users;");
  },
  new Class[]{ Exception.class }, // For these exceptions rollback will be performed
  new Class[]{ FileNotFoundException.class } // For these exceptions rollback will not be performed
);
```



