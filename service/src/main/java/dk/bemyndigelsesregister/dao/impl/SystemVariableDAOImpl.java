package dk.bemyndigelsesregister.dao.impl;

import dk.bemyndigelsesregister.dao.SystemVariableDAO;
import dk.bemyndigelsesregister.domain.SystemVariable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

@Repository
public class SystemVariableDAOImpl extends AbstractDAOImpl<SystemVariable> implements SystemVariableDAO {

    public SystemVariableDAOImpl() {
        super("system_variable");
    }

    @Override
    public SystemVariable get(long id) {
        return queryForObject("SELECT * FROM " + tableName + " v WHERE v.id = :id", Collections.singletonMap("id", id));
    }

    @Override
    public SystemVariable getByName(String name) {
        return queryForObject("SELECT * FROM " + tableName + " v WHERE v.name = :name", Collections.singletonMap("name", name));
    }

    @Override
    protected MapSqlParameterSource getSaveParameters(SystemVariable systemVariable) {
        return new MapSqlParameterSource()
                .addValue("name", systemVariable.getName())
                .addValue("value", systemVariable.getValue());
    }

    @Override
    protected AbstractRowMapper<SystemVariable> getRowMapper() {
        return new AbstractRowMapper<>() {
            @Override
            protected SystemVariable map(ResultSet rs) throws SQLException {
                SystemVariable v = new SystemVariable();

                v.setName(rs.getString("name"));
                v.setValue(rs.getString("value"));

                return v;
            }
        };
    }
}
