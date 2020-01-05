package hr.yeti.rudimentary.sql;

import java.util.Map;

/**
 * Function used in {@link Sql#row(java.lang.String, hr.yeti.rudimentary.sql.RowMapper, java.lang.Object...)
 * } and {@link Sql#rows(java.lang.String, hr.yeti.rudimentary.sql.RowMapper, java.lang.Object...) }
 * methods to describe how rows are mapped to some object.
 *
 * @param <T> Mapped row type.
 *
 * @author vedransmid@yeti-it.hr
 */
@FunctionalInterface
public interface RowMapper<T> {

    /**
     * Mapping describe method.
     *
     * @param row Sql query row result.
     * @return New mapped object.
     */
    T map(Map<String, Object> row);

}
