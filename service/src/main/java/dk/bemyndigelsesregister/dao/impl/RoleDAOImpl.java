package dk.bemyndigelsesregister.dao.impl;

import dk.bemyndigelsesregister.dao.RoleDAO;
import dk.bemyndigelsesregister.domain.Role;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Repository
public class RoleDAOImpl extends AbstractDAOImpl<Role> implements RoleDAO {

    public RoleDAOImpl() {
        super("arbejdsfunktion");
    }

    @Override
    public Role findByCode(Long systemId, String code) {
        return queryForObject(new MapSqlParameterSource()
                .addValue("linked_system_id", systemId)
                .addValue("kode", code));
    }

    @Override
    public List<Role> findBySystem(Long systemId) {
        return queryForList(new MapSqlParameterSource().addValue("linked_system_id", systemId));
    }

    @Override
    protected MapSqlParameterSource getSaveParameters(Role role) {
        return new MapSqlParameterSource()
                .addValue("kode", role.getCode())
                .addValue("beskrivelse", role.getDescription())
                .addValue("uddannelseskoder", educationCodesToString(role.getEducationCodes()))
                .addValue("linked_system_id", role.getSystem().getId());
    }

    @Override
    protected AbstractRowMapper<Role> getRowMapper() {
        return new AbstractRowMapper<>() {
            @Override
            protected Role map(ResultSet rs) throws SQLException {
                Role s = new Role();

                s.setCode(rs.getString("kode"));
                s.setDescription(rs.getString("beskrivelse"));
                s.setEducationCodes(educationCodesToList(rs.getString("uddannelseskoder")));

                return s;
            }
        };
    }

    private String educationCodesToString(List<String> educationCodes) {
        if (educationCodes == null || educationCodes.isEmpty()) {
            return null;
        }
        return String.join(",",educationCodes);
    }

    private List<String> educationCodesToList(String educationCodes) {
        if (educationCodes == null || educationCodes.isBlank()) {
            return null;
        }
        return List.of(educationCodes.split(","));
    }
}
