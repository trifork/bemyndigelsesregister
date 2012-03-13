package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.springjdbc;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.inject.Inject;
import javax.sql.DataSource;

import static java.util.Collections.singletonMap;

public abstract class SupportDao<T> implements InitializingBean {
    protected NamedParameterJdbcTemplate jdbcTemplate;
    protected final String tableName;
    protected final RowMapper<T> rowMapper;

    @Inject
    DataSource dataSource;

    protected SupportDao(String tableName, RowMapper<T> rowMapper) {
        this.tableName = tableName;
        this.rowMapper = rowMapper;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public T get(long id) {
        String sql = String.format("SELECT * FROM %s where id = :id", tableName);
        return jdbcTemplate.queryForObject(
                sql,
                singletonMap("id", id),
                rowMapper
        );
    }
}
