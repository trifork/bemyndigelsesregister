package dk.bemyndigelsesregister.dao.impl;

import dk.bemyndigelsesregister.dao.PermissionDAO;
import dk.bemyndigelsesregister.domain.Permission;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PermissionDAOImpl extends AbstractDAOImpl<Permission> implements PermissionDAO {

    public PermissionDAOImpl() {
        super("rettighed");
    }

    @Override
    public Permission findByCode(Long systemId, String code) {

        return queryForObject(new MapSqlParameterSource()
                .addValue("linked_system_id", systemId)
                .addValue("kode", code));
    }

    @Override
    public List<Permission> findBySystem(Long systemId) {
        return queryForList(new MapSqlParameterSource().addValue("linked_system_id", systemId));
    }

    @Override
    protected MapSqlParameterSource getSaveParameters(Permission permission) {
        return new MapSqlParameterSource()
                .addValue("kode", permission.getCode())
                .addValue("beskrivelse", permission.getDescription())
                .addValue("linked_system_id", permission.getSystem().getId());
    }

    @Override
    protected AbstractRowMapper<Permission> getRowMapper() {
        return new AbstractRowMapper<>() {
            @Override
            protected Permission map(ResultSet rs) throws SQLException {
                Permission p = new Permission();

                p.setCode(rs.getString("kode"));
                p.setDescription(rs.getString("beskrivelse"));

                return p;
            }
        };
    }
}
