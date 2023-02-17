package dk.bemyndigelsesregister.dao.impl;

import dk.bemyndigelsesregister.domain.DomainObject;
import dk.bemyndigelsesregister.ws.RequestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractDAOImpl<T extends DomainObject> {
    private static final Logger logger = LogManager.getLogger(AbstractDAOImpl.class);

    protected NamedParameterJdbcTemplate jdbcTemplate;
    protected String tableName;

    public AbstractDAOImpl(String tableName) {
        this.tableName = tableName;
    }

    @Autowired
    @Qualifier("bemDataSource")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public T get(long id) {
        return queryForObject("SELECT * FROM " + tableName + " s WHERE s.id = :id", Collections.singletonMap("id", id));
    }

    public void save(T t) {
        MapSqlParameterSource paramMap = getSaveParameters(t)
                .addValue("sidst_modificeret", Instant.now())
                .addValue("sidst_modificeret_af", RequestContext.get().getActingUser());

        if (t.getId() == null) {
            String sql = "INSERT INTO " + tableName + " (" + String.join(", ", paramMap.getParameterNames()) + ") VALUES (" + String.join(", ", getColonPrefixedParamNames(paramMap)) + ")";
            logger.debug(sql, paramMap);

            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(sql, paramMap, keyHolder);
            t.setId(keyHolder.getKey().longValue());
        } else {
            StringBuilder b = new StringBuilder();
            for (String paramName : paramMap.getParameterNames()) {
                if (b.length() > 0) {
                    b.append(", ");
                }
                b.append(paramName).append(" = :").append(paramName);
            }
            String sql = "UPDATE " + tableName + " SET " + b + " WHERE id = :id";

            paramMap.addValue("id", t.getId());

            logger.debug(sql, paramMap);
            jdbcTemplate.update(sql, paramMap);
        }
    }

    public void remove(long id) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue("id", id);
        String sql = "DELETE FROM " + tableName + " WHERE id = :id";

        logger.debug(sql, paramMap);

        jdbcTemplate.update(sql, paramMap);
    }

    protected abstract MapSqlParameterSource getSaveParameters(T t);

    protected abstract AbstractRowMapper<T> getRowMapper();

    protected T queryForObject(MapSqlParameterSource paramMap) {
        StringBuilder b = new StringBuilder();
        for (String paramName : paramMap.getParameterNames()) {
            if (b.length() > 0) {
                b.append(" AND ");
            }
            b.append(paramName).append(" = :").append(paramName);
        }

        return queryForObject("SELECT * FROM " + tableName + " WHERE " + b, paramMap);
    }

    protected T queryForObject(String sql, MapSqlParameterSource paramMap) {
        try {
            logger.debug(sql, paramMap);
            return jdbcTemplate.queryForObject(sql, paramMap, getRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    protected T queryForObject(String sql, Map<String, Object> paramMap) {
        try {
            logger.debug(sql, paramMap);
            return jdbcTemplate.queryForObject(sql, paramMap, getRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    protected List<T> queryForList(String sql) {
        return queryForList(sql, new MapSqlParameterSource());
    }

    protected List<T> queryForList(MapSqlParameterSource paramMap) {
        String sql = "SELECT * FROM " + tableName;

        if (paramMap.getParameterNames().length > 0) {
            StringBuilder b = new StringBuilder();
            for (String paramName : paramMap.getParameterNames()) {
                if (b.length() > 0) {
                    b.append(" AND ");
                }
                b.append(paramName).append(" = :").append(paramName);
            }
            sql += " WHERE " + b;
        }

        return queryForList(sql, paramMap);
    }

    protected List<T> queryForList(String sql, MapSqlParameterSource paramMap) {
        logger.debug(sql, paramMap);
        return jdbcTemplate.query(sql, paramMap, getRowMapper());
    }

    protected List<Long> queryForIdList(String sql, MapSqlParameterSource paramMap) {
        logger.debug(sql, paramMap);
        return jdbcTemplate.query(sql, paramMap, (rs, rowNum) -> rs.getLong("id"));
    }

    private List<String> getColonPrefixedParamNames(MapSqlParameterSource paramMap) {
        List<String> colonPrefixedNames = new LinkedList<>();
        for (String paramName : paramMap.getParameterNames()) {
            colonPrefixedNames.add(":" + paramName);
        }
        return colonPrefixedNames;
    }

}
