package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationDao;
import dk.nsi.bemyndigelse._2016._01._01.State;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Collection;
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
    public List<Delegation> findInPeriod(String systemCode, String delegatorCpr, String delegateeCpr, String delegateeCvr, String roleCode, State state, DateTime effectiveFrom, DateTime effectiveTo) {
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
    public List<Delegation> findByLastModifiedGreaterThanOrEquals(DateTime lastModified) {
        return query().where().ge("lastModified", lastModified).findList();
    }
}
