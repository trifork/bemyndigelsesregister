package dk.bemyndigelsesregister.dao.impl;

import dk.bemyndigelsesregister.dao.WhitelistDAO;
import dk.bemyndigelsesregister.domain.Whitelist;
import dk.bemyndigelsesregister.domain.WhitelistType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class WhitelistDAOImpl extends AbstractDAOImpl<Whitelist> implements WhitelistDAO {

    public WhitelistDAOImpl() {
        super("whitelist");
    }

    @Override
    public List<Whitelist> get(String name, WhitelistType type, String subjectId) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("type", type.name())
                .addValue("subjectId", subjectId);

        String sql = "SELECT * FROM " + tableName + " w WHERE w.name = :name AND w.whitelist_type = :type AND w.subject_id = :subjectId";
        return queryForList(sql, paramMap);
    }

    @Override
    public boolean exists(String name, WhitelistType type, String subjectId) {
        return !get(name, type, subjectId).isEmpty();
    }

    @Override
    protected MapSqlParameterSource getSaveParameters(Whitelist whitelist) {
        throw new UnsupportedOperationException("Save whitelist is not supported");
    }

    @Override
    protected AbstractRowMapper<Whitelist> getRowMapper() {
        return new AbstractRowMapper<>() {
            @Override
            protected Whitelist map(ResultSet rs) throws SQLException {
                Whitelist w = new Whitelist();

                w.setWhitelistType(WhitelistType.valueOf(rs.getString("whitelist_type")));
                w.setName(rs.getString("name"));
                w.setSubjectId(rs.getString("subject_id"));

                return w;
            }
        };
    }
}
