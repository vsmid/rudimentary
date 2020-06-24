package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Json;
import hr.yeti.rudimentary.http.content.Text;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.sql.Sql;
import hr.yeti.rudimentary.sql.SqlQueryDef;
import java.util.Map;

public class SqlEndpoint implements HttpEndpoint<Text, Json> {

    @Override
    public String path() {
        return "/sql";
    }

    @Override
    public Json response(Request<Text> request) {
        Sql.query().row("select * from users where id=?;", 1);

        Sql.tx((sql) -> {
            sql.update("insert into users(id, name) values(1, 'M');");
            sql.update("insert into users(id, name) values(2, 'M');");
            sql.update("insert into users(id, name) values(3, 'M');");
            return sql.rows("select * from users;");
        });

        // Repository usage example
        Map<String, Object> user = Sql.query(USER.getById(1));
        System.out.println(user.toString());

        return new Json(user);
    }

    // Repository example :-)
    public static class USER {

        static SqlQueryDef<Map<String, Object>> getById(long id) {
            return (sql) -> {
                return sql.row("select * from users where id=?;", id);
            };
        }

    }
}
