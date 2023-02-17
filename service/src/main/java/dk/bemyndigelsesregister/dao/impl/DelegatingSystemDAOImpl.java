package dk.bemyndigelsesregister.dao.impl;

import dk.bemyndigelsesregister.dao.DelegatingSystemDAO;
import dk.bemyndigelsesregister.dao.DomainDAO;
import dk.bemyndigelsesregister.domain.DelegatingSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Repository
public class DelegatingSystemDAOImpl extends AbstractDAOImpl<DelegatingSystem> implements DelegatingSystemDAO {
    @Autowired
    private DomainDAO domainDAO;

    public DelegatingSystemDAOImpl() {
        super("linked_system");
    }

    @Override
    public DelegatingSystem findByCode(String code) {
        return queryForObject("SELECT * FROM " + tableName + " s WHERE s.kode = :code", Collections.singletonMap("code", code));
    }

    @Override
    public List<DelegatingSystem> findByDomain(long domainId) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue("domainId", domainId);
        String sql = "SELECT * FROM " + tableName + " WHERE domaene_id = :domainId";
        return queryForList(sql, paramMap);
    }

    @Override
    public List<DelegatingSystem> findByLastModifiedGreaterThanOrEquals(Instant lastModified) {
        String sql = "SELECT * FROM " + tableName + " WHERE sidst_modificeret >= :sidst_modificeret";
        return queryForList(sql, new MapSqlParameterSource().addValue("sidst_modificeret", lastModified));
    }

    @Override
    protected MapSqlParameterSource getSaveParameters(DelegatingSystem delegatingSystem) {
        return new MapSqlParameterSource()
                .addValue("kode", delegatingSystem.getCode())
                .addValue("beskrivelse", delegatingSystem.getDescription())
                .addValue("domaene_id", delegatingSystem.getDomain().getId());
    }

    @Override
    protected AbstractRowMapper<DelegatingSystem> getRowMapper() {
        return new AbstractRowMapper<>() {
            @Override
            protected DelegatingSystem map(ResultSet rs) throws SQLException {
                DelegatingSystem s = new DelegatingSystem();

                s.setCode(rs.getString("kode"));
                s.setDescription(rs.getString("beskrivelse"));
                s.setDomain(domainDAO.get(rs.getLong("domaene_id")));

                return s;
            }
        };
    }
}
