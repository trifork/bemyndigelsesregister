package dk.bemyndigelsesregister.dao.impl;

import dk.bemyndigelsesregister.dao.DomainDAO;
import dk.bemyndigelsesregister.domain.Domain;
import dk.bemyndigelsesregister.domain.Permission;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class DomainDAOImpl extends AbstractDAOImpl<Domain> implements DomainDAO {

    public DomainDAOImpl() {
        super("domaene");
    }

    @Override
    public Domain findByCode(String code) {
        return queryForObject(new MapSqlParameterSource().addValue("kode", code));
    }

    @Override
    protected MapSqlParameterSource getSaveParameters(Domain domain) {
        return new MapSqlParameterSource().addValue("kode", domain.getCode());
    }

    @Override
    protected AbstractRowMapper<Domain> getRowMapper() {
        return new AbstractRowMapper<>() {
            @Override
            protected Domain map(ResultSet rs) throws SQLException {
                Domain d = new Domain();
                d.setCode(rs.getString("kode"));
                return d;
            }
        };
    }
}
