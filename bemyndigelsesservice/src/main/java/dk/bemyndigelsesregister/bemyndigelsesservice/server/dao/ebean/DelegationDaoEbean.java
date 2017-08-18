package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import com.avaje.ebean.ExpressionList;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Metadata;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Status;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
    public List<Delegation> findByDelegatorCpr(String delegatorCpr) {
        return query().where().eq("delegatorCpr", delegatorCpr).findList();
    }

    @Override
    public List<Delegation> findByDelegateeCpr(String delegateeCpr) {
        return query().where().eq("delegateeCpr", delegateeCpr).findList();
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
        if(fromIncluding != null)
            expression = expression.ge("lastModified", fromIncluding);
        if(toExcluding != null)
            expression = expression.lt("lastModified", toExcluding);

        List<Object> ids = expression.findIds();
        if (ids != null)
            for (Object id : ids)
                result.add((Long)id);

        return result;
    }

    @Override
    public List<Long> findWithAsterisk(String systemCode, DateTime validDate) {
        List<Long> result = new LinkedList<>();

        List<Object> ids = query().where().eq("systemCode", systemCode).gt("effectiveTo", validDate).eq("delegationPermissions.permissionCode", Metadata.ASTERISK_PERMISSION_CODE).findIds();
        if (ids != null)
            for (Object id : ids)
                result.add((Long)id);

        return result;
    }
}
