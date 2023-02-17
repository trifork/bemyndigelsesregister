package dk.bemyndigelsesregister.dao.impl;

import dk.bemyndigelsesregister.dao.DelegationDAO;
import dk.bemyndigelsesregister.domain.Delegation;
import dk.bemyndigelsesregister.domain.ExpirationInfo;
import dk.bemyndigelsesregister.domain.Metadata;
import dk.bemyndigelsesregister.domain.Status;
import dk.bemyndigelsesregister.util.DateUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

/**
 * BEM 2.0 bemyndigelse
 */
@Repository
public class DelegationDAOImpl extends AbstractDAOImpl<Delegation> implements DelegationDAO {

    public DelegationDAOImpl() {
        super("bemyndigelse20");
    }

    @Override
    public List<Delegation> list() {
        return queryForList(new MapSqlParameterSource());
    }

    @Override
    public Delegation findByCode(String code) {
        return queryForObject(new MapSqlParameterSource().addValue("kode", code));
    }

    @Override
    public List<Delegation> findByDelegatorCpr(String delegatorCpr, Instant effectiveFrom, Instant effectiveTo) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource()
                .addValue("bemyndigende_cpr", delegatorCpr)
                .addValue("gyldig_fra", effectiveFrom)
                .addValue("gyldig_til", effectiveTo);

        String sql = "SELECT * FROM " + tableName + " WHERE bemyndigende_cpr = :bemyndigende_cpr";
        if (effectiveFrom != null) {
            sql += " AND gyldig_til > :gyldig_fra";
        }
        if (effectiveTo != null) {
            sql += " AND gyldig_fra <= :gyldig_til";
        }

        return queryForList(sql, paramMap);
    }

    @Override
    public List<Delegation> findByDelegateeCpr(String delegateeCpr, Instant effectiveFrom, Instant effectiveTo) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource()
                .addValue("bemyndigede_cpr", delegateeCpr)
                .addValue("gyldig_fra", effectiveFrom)
                .addValue("gyldig_til", effectiveTo);

        String sql = "SELECT * FROM " + tableName + " WHERE bemyndigede_cpr = :bemyndigede_cpr";
        if (effectiveFrom != null) {
            sql += " AND gyldig_til > :gyldig_fra";
        }
        if (effectiveTo != null) {
            sql += " AND gyldig_fra <= :gyldig_til";
        }

        return queryForList(sql, paramMap);
    }

    @Override
    public List<Delegation> findInPeriod(String systemCode, String delegatorCpr, String delegateeCpr, String delegateeCvr, String roleCode, Instant effectiveFrom, Instant effectiveTo) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource()
                .addValue("linked_system_kode", systemCode)
                .addValue("bemyndigende_cpr", delegatorCpr)
                .addValue("bemyndigede_cpr", delegateeCpr)
                .addValue("bemyndigede_cvr", delegateeCvr)
                .addValue("arbejdsfunktion_kode", roleCode)
                .addValue("gyldig_fra", effectiveFrom)
                .addValue("gyldig_til", effectiveTo);

        String sql = "SELECT * FROM " + tableName +
                " WHERE linked_system_kode = :linked_system_kode" +
                " AND bemyndigende_cpr = :bemyndigende_cpr" +
                " AND bemyndigede_cpr = :bemyndigede_cpr" +
                " AND bemyndigede_cvr = :bemyndigede_cvr" +
                " AND arbejdsfunktion_kode = :arbejdsfunktion_kode";
        if (effectiveFrom != null) {
            sql += " AND gyldig_til > :gyldig_fra";
        }
        if (effectiveTo != null) {
            sql += " AND gyldig_fra <= :gyldig_til";
        }

        return queryForList(sql, paramMap);
    }

    @Override
    public List<Delegation> findByCodes(Collection<String> codes) {
        List<String> quotedCodes = new LinkedList<>();
        for (String code : codes) {
            quotedCodes.add("'" + code + "'");
        }

        String sql = "SELECT * FROM " + tableName + " WHERE kode IN (" + String.join(", ", quotedCodes) + ")";
        return queryForList(sql);
    }

    @Override
    public List<Long> findByModifiedInPeriod(Instant fromIncluding, Instant toExcluding) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource()
                .addValue("status", Status.GODKENDT.name())
                .addValue("fra", fromIncluding)
                .addValue("til", toExcluding);

        String sql = "SELECT * FROM " + tableName + " WHERE status = :status";
        if (fromIncluding != null) {
            sql += " AND sidst_modificeret > :fra";
        }
        if (toExcluding != null) {
            sql += " AND sidst_modificeret <= :til";
        }

        return queryForIdList(sql, paramMap);
    }

    @Override
    public List<Long> findWithAsterisk(String systemCode, Instant validDate) {
        String sql = "SELECT t.id FROM " + tableName + " t, bemyndigelse20_rettighed r WHERE t.linked_system_kode = :systemCode" +
                " AND t.gyldig_til > :validDate AND t.id = r.bemyndigelse20_id AND r.rettighed_kode = :permissionCode";
        MapSqlParameterSource paramMap = new MapSqlParameterSource()
                .addValue("systemCode", systemCode)
                .addValue("validDate", validDate)
                .addValue("permissionCode", Metadata.ASTERISK_PERMISSION_CODE);

        return queryForIdList(sql, paramMap);
    }

    @Override
    public List<Long> findExpiredBefore(Instant date, int maxRecords) {
        String sql = "SELECT id FROM " + tableName + " WHERE gyldig_til < :date LIMIT :limit";
        MapSqlParameterSource paramMap = new MapSqlParameterSource()
                .addValue("date", date)
                .addValue("limit", maxRecords);

        return queryForIdList(sql, paramMap);
    }

    @Override
    public ExpirationInfo getExpirationInfo(String cpr, int days) {
        int delegationCount = 0;
        int daysToExpiration = 0;
        Set<String> delegateeCprs = new HashSet<>();

        Instant effectiveFrom = Instant.now();
        Instant effectiveTo = DateUtils.plusDays(effectiveFrom, days);

        List<Delegation> delegations = new LinkedList<>();

        MapSqlParameterSource paramMap = new MapSqlParameterSource()
                .addValue("cpr", cpr)
                .addValue("status", Status.GODKENDT.name())
                .addValue("gyldig_fra", effectiveFrom)
                .addValue("gyldig_til", effectiveTo);

        // valid delegations by person that will soon expire
        String sql = "SELECT * FROM " + tableName + " WHERE bemyndigende_cpr = :cpr AND status = :status" +
                " AND gyldig_fra < :gyldig_fra AND gyldig_til > :gyldig_fra AND gyldig_til < :gyldig_til";
        List<Delegation> delegatorDelegations = queryForList(sql, paramMap);
        delegations.addAll(delegatorDelegations);

        // delegations to person that will soon expire
        sql = "SELECT * FROM " + tableName + " WHERE bemyndigede_cpr = :cpr AND status = :status" +
                " AND gyldig_fra < :gyldig_fra AND gyldig_til > :gyldig_fra AND gyldig_til < :gyldig_til";
        List<Delegation> delegateeDelegations = queryForList(sql, paramMap);
        delegations.addAll(delegateeDelegations);

        // find no. of days until first delegation will expire
        for (Delegation delegation : delegations) {
            delegationCount++;
            delegateeCprs.add(delegation.getDelegateeCpr());
            int d = 1 + DateUtils.daysBetween(effectiveFrom, delegation.getEffectiveTo());
            if (daysToExpiration == 0 || d < daysToExpiration) {
                daysToExpiration = d;
            }
        }

        ExpirationInfo info = new ExpirationInfo();
        info.setDelegateeCount(delegateeCprs.size());
        info.setDelegationCount(delegationCount);
        info.setDaysToFirstExpiration(daysToExpiration);

        // identify number of delegations/delegatees that expires on that day
        delegationCount = 0;
        delegateeCprs.clear();

        for (Delegation delegation : delegations) {
            int d = 1 + DateUtils.daysBetween(effectiveFrom, delegation.getEffectiveTo());
            if (d == daysToExpiration) {
                delegationCount++;
                delegateeCprs.add(delegation.getDelegateeCpr());
            }
        }

        info.setFirstExpiryDelegationCount(delegationCount);
        info.setFirstExpiryDelegateeCount(delegateeCprs.size());

        return info;
    }

    @Override
    protected MapSqlParameterSource getSaveParameters(Delegation delegation) {
        return new MapSqlParameterSource()
                .addValue("kode", delegation.getCode())
                .addValue("bemyndigende_cpr", delegation.getDelegatorCpr())
                .addValue("bemyndigede_cpr", delegation.getDelegateeCpr())
                .addValue("bemyndigede_cvr", delegation.getDelegateeCvr())
                .addValue("godkendelsesdato", delegation.getCreated())
                .addValue("gyldig_fra", delegation.getEffectiveFrom())
                .addValue("gyldig_til", delegation.getEffectiveTo())
                .addValue("status", delegation.getState().name())
                .addValue("linked_system_kode", delegation.getSystemCode())
                .addValue("arbejdsfunktion_kode", delegation.getRoleCode())
                .addValue("versionsid", delegation.getVersionsid());
    }

    @Override
    protected AbstractRowMapper<Delegation> getRowMapper() {
        return new AbstractRowMapper<>() {
            @Override
            protected Delegation map(ResultSet rs) throws SQLException {
                Delegation d = new Delegation();

                d.setCode(rs.getString("kode"));
                d.setDelegatorCpr(rs.getString("bemyndigende_cpr"));
                d.setDelegateeCpr(rs.getString("bemyndigede_cpr"));
                d.setDelegateeCvr(rs.getString("bemyndigede_cvr"));
                d.setCreated(getInstant(rs, "godkendelsesdato"));
                d.setEffectiveFrom(getInstant(rs, "gyldig_fra"));
                d.setEffectiveTo(getInstant(rs, "gyldig_til"));
                d.setState(Status.valueOf(rs.getString("status")));
                d.setSystemCode(rs.getString("linked_system_kode"));
                d.setRoleCode(rs.getString("arbejdsfunktion_kode"));
                d.setVersionsid(rs.getInt("versionsid"));

                return d;
            }
        };
    }
}
