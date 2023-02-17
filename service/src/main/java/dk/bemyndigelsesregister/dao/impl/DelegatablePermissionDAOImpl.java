package dk.bemyndigelsesregister.dao.impl;

import dk.bemyndigelsesregister.dao.DelegatablePermissionDAO;
import dk.bemyndigelsesregister.dao.PermissionDAO;
import dk.bemyndigelsesregister.dao.RoleDAO;
import dk.bemyndigelsesregister.domain.DelegatablePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Repository
public class DelegatablePermissionDAOImpl extends AbstractDAOImpl<DelegatablePermission> implements DelegatablePermissionDAO {

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private PermissionDAO permissionDAO;

    public DelegatablePermissionDAOImpl() {
        super("delegerbar_rettighed");
    }

    @Override
    public DelegatablePermission findByPermissionAndRole(Long permissionId, Long roleId) {
        return queryForObject(new MapSqlParameterSource()
                .addValue("arbejdsfunktion_id", roleId)
                .addValue("rettighedskode_id", permissionId));
    }

    @Override
    public List<DelegatablePermission> findBySystemAndRole(Long systemId, Long roleId) {
        String sql = "SELECT d.* from delegerbar_rettighed d, rettighed r WHERE d.rettighedskode_id = r.id AND r.linked_system_id = :systemId AND d.arbejdsfunktion_id = :roleId";

        MapSqlParameterSource paramMap = new MapSqlParameterSource()
                .addValue("systemId", systemId)
                .addValue("roleId", roleId);
        return queryForList(sql, paramMap);
    }

    @Override
    public List<DelegatablePermission> findBySystem(Long systemId) {
        String sql = "SELECT d.* from delegerbar_rettighed d, rettighed r WHERE d.rettighedskode_id = r.id AND r.linked_system_id = :systemId";
        return queryForList(sql, new MapSqlParameterSource().addValue("systemId", systemId));
    }

    @Override
    protected MapSqlParameterSource getSaveParameters(DelegatablePermission delegatablePermission) {
        return new MapSqlParameterSource()
                .addValue("arbejdsfunktion_id", delegatablePermission.getRole().getId())
                .addValue("rettighedskode_id", delegatablePermission.getPermission().getId())
                .addValue("delegerbar", delegatablePermission.isDelegatable());
    }

    @Override
    protected AbstractRowMapper<DelegatablePermission> getRowMapper() {
        return new AbstractRowMapper<>() {
            @Override
            protected DelegatablePermission map(ResultSet rs) throws SQLException {
                DelegatablePermission p = new DelegatablePermission();

                p.setRole(roleDAO.get(rs.getLong("arbejdsfunktion_id")));
                p.setPermission(permissionDAO.get(rs.getLong("rettighedskode_id")));
                p.setDelegatable(rs.getBoolean("delegerbar"));

                return p;
            }
        };
    }
}
