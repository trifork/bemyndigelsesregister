package dk.bemyndigelsesregister.dao.impl;

import dk.bemyndigelsesregister.domain.DomainObject;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public abstract class AbstractRowMapper<T extends DomainObject> implements RowMapper<T> {
    @Override
    public T mapRow(ResultSet rs, int row) throws SQLException {
        T t = map(rs);

        t.setId(rs.getLong("id"));
        t.setLastModified(getInstant(rs, "sidst_modificeret"));
        t.setLastModifiedBy(rs.getString("sidst_modificeret_af"));

        return t;

    }

    protected abstract T map(ResultSet rs) throws SQLException;

    protected Instant getInstant(ResultSet rs, String columnLabel) throws SQLException {
        Timestamp d = rs.getTimestamp(columnLabel);
        return d != null ? d.toInstant() : null;
    }
}
