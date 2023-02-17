package dk.bemyndigelsesregister.dao.impl;

import dk.bemyndigelsesregister.dao.DelegationPermissionDAO;
import dk.bemyndigelsesregister.domain.DelegationPermission;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DelegationPermissionDAOImpl extends AbstractDAOImpl<DelegationPermission> implements DelegationPermissionDAO {
    private static final Logger logger = LogManager.getLogger(DelegationPermissionDAOImpl.class);

    public DelegationPermissionDAOImpl() {
        super("bemyndigelse20_rettighed");
    }

    @Override
    public List<DelegationPermission> findByDelegationId(long delegationId) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue("delegationId", delegationId);
        String sql = "SELECT * FROM " + tableName + " WHERE bemyndigelse20_id = :delegationId";
        return queryForList(sql, paramMap);
    }

    @Override
    public void removeByDelegationId(long delegationId) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue("delegationId", delegationId);
        String sql = "DELETE FROM " + tableName + " WHERE bemyndigelse20_id = :delegationId";

        logger.debug(sql, paramMap);

        jdbcTemplate.update(sql, paramMap);
    }

    @Override
    protected MapSqlParameterSource getSaveParameters(DelegationPermission delegationPermission) {
        return new MapSqlParameterSource()
                .addValue("kode", delegationPermission.getCode())
                .addValue("rettighed_kode", delegationPermission.getPermissionCode())
                .addValue("bemyndigelse20_id", delegationPermission.getDelegationId());
    }

    @Override
    protected AbstractRowMapper<DelegationPermission> getRowMapper() {
        return new AbstractRowMapper<>() {
            @Override
            protected DelegationPermission map(ResultSet rs) throws SQLException {
                DelegationPermission p = new DelegationPermission();

                p.setCode(rs.getString("kode"));
                p.setPermissionCode(rs.getString("rettighed_kode"));
                p.setDelegationId(rs.getLong("bemyndigelse20_id"));

                return p;
            }
        };
    }

}
