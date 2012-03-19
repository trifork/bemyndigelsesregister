package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.springjdbc;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Authorization;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.AuthorizationDao;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorizationDaoJdbc extends SupportDao<Authorization> implements AuthorizationDao {
    public AuthorizationDaoJdbc() {
        super("Authorization", new RowMapper<Authorization>() {
            @Override
            public Authorization mapRow(ResultSet rs, int i) throws SQLException {
                return new Authorization(rs.getLong("id"), rs.getString("name"));
            }
        });
    }
}
