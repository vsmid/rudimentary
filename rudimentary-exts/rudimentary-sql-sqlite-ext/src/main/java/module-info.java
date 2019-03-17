module RudimentarySqlSqliteExt {
  requires hr.yeti.rudimentary.api;
  requires sqlite.jdbc;

  exports hr.yeti.rudimentary.exts.rudimentary.sql.sqlite.ext;

  provides hr.yeti.rudimentary.sql.spi.JdbcConnectionPool with hr.yeti.rudimentary.exts.rudimentary.sql.sqlite.ext.SqliteJdbcConnectionPool;
}
