package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import com.avaje.ebean.ExpressionList;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.ExpirationInfo;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Metadata;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Status;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationDao;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * BEM 2.0 bemyndigelse
 * Created by obj on 02-02-2016.
 */
@Repository
public class DelegationDaoEbean extends SupportDao<Delegation> implements DelegationDao {
    protected DelegationDaoEbean() {
        super(Delegation.class);
    }

    @Override
    public List<Delegation> findByDelegatorCpr(String delegatorCpr, DateTime effectiveFrom, DateTime effectiveTo) {
        ExpressionList<Delegation> query = query().where().eq("delegatorCpr", delegatorCpr);
        if (effectiveFrom != null) {
            query = query.gt("effectiveTo", effectiveFrom);
        }
        if (effectiveTo != null) {
            query = query.le("effectiveFrom", effectiveTo);
        }
        return query.findList();
    }

    @Override
    public List<Delegation> findByDelegateeCpr(String delegateeCpr, DateTime effectiveFrom, DateTime effectiveTo) {
        ExpressionList<Delegation> query = query().where().eq("delegateeCpr", delegateeCpr);
        if (effectiveFrom != null) {
            query = query.gt("effectiveTo", effectiveFrom);
        }
        if (effectiveTo != null) {
            query = query.le("effectiveFrom", effectiveTo);
        }
        return query.findList();
    }

    @Override
    public List<Delegation> findInPeriod(String systemCode, String delegatorCpr, String delegateeCpr, String delegateeCvr, String roleCode, Status state, DateTime effectiveFrom, DateTime effectiveTo) {
        return query().where()
                .eq("systemCode", systemCode)
                .eq("delegatorCpr", delegatorCpr)
                .eq("delegateeCpr", delegateeCpr)
                .eq("delegateeCvr", delegateeCvr)
                .eq("roleCode", roleCode).and(
                        expr().le("effectiveFrom", effectiveTo),
                        expr().gt("effectiveTo", effectiveFrom)
                ).findList();
    }

    @Override
    public List<Delegation> findByCodes(Collection<String> codes) {
        return query().where().in("code", codes).findList();
    }

    @Override
    public List<Long> findByModifiedInPeriod(DateTime fromIncluding, DateTime toExcluding) {
        List<Long> result = new LinkedList<>();

        ExpressionList<Delegation> expression = query().where().eq("state", Status.GODKENDT);
        if (fromIncluding != null)
            expression = expression.ge("lastModified", fromIncluding);
        if (toExcluding != null)
            expression = expression.lt("lastModified", toExcluding);

        List<Object> ids = expression.findIds();
        if (ids != null)
            for (Object id : ids)
                result.add((Long) id);

        return result;
    }

    @Override
    public List<Long> findWithAsterisk(String systemCode, DateTime validDate) {
        List<Long> result = new LinkedList<>();

        List<Object> ids = query().where().eq("systemCode", systemCode).gt("effectiveTo", validDate).eq("delegationPermissions.permissionCode", Metadata.ASTERISK_PERMISSION_CODE).findIds();
        if (ids != null)
            for (Object id : ids)
                result.add((Long) id);

        return result;
    }

    @Override
    public ExpirationInfo getExpirationInfo(String delegatorCpr, int days) {
        int delegationCount = 0;
        int daysToExpiration = 0;
        Set<String> delegateeCprs = new HashSet<>();

        DateTime effectiveFrom = DateTime.now();
        DateTime effectiveTo = effectiveFrom.plusDays(days);

        List<Delegation> delegations = query().where().eq("delegatorCpr", delegatorCpr).lt("effectiveFrom", effectiveFrom).gt("effectiveTo", effectiveFrom).lt("effectiveTo", effectiveTo).eq("state", Status.GODKENDT.value()).findList();

        // find no. of days to first delegation will expire
        if (delegations != null) {
            for (Delegation delegation : delegations) {
                delegationCount++;
                delegateeCprs.add(delegation.getDelegateeCpr());
                int d = 1 + Days.daysBetween(effectiveFrom, delegation.getEffectiveTo()).getDays();
                if (daysToExpiration == 0 || d < daysToExpiration) {
                    daysToExpiration = d;
                }
            }
        }

        ExpirationInfo info = new ExpirationInfo();
        info.setDelegateeCount(delegateeCprs.size());
        info.setDelegationCount(delegationCount);
        info.setDaysToFirstExpiration(daysToExpiration);

        // identify number of delegations/delegatees that expires on that day
        delegationCount = 0;
        delegateeCprs.clear();
        if (delegations != null) {
            for (Delegation delegation : delegations) {
                int d = 1 + Days.daysBetween(effectiveFrom, delegation.getEffectiveTo()).getDays();
                if (d == daysToExpiration) {
                    delegationCount++;
                    delegateeCprs.add(delegation.getDelegateeCpr());
                }
            }
        }
        info.setFirstExpiryDelegationCount(delegationCount);
        info.setFirstExpiryDelegateeCount(delegateeCprs.size());

        return info;
    }
}
