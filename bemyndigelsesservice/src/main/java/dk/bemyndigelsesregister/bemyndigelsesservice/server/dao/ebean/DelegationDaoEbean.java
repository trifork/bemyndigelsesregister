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
    public Delegation findById(String delegationId) {
        return query().where().eq("kode", delegationId).findUnique();
    }

    @Override
    public List<Delegation> findByInPeriod(String system, String delegatorCpr, String delegateeCpr, String delegateeCvr, String role, State state, DateTime effectiveFrom, DateTime effectiveTo) {
        return query().where()
                .eq("delegatingSystem", system)
                .eq("delegatorCpr", delegatorCpr)
                .eq("delegateeCpr", delegateeCpr)
                .eq("delegateeCvr", delegateeCvr)
                .eq("role", role)
                .eq("state", state).and(
                        expr().le("effectiveFrom", effectiveTo),
                        expr().gt("effectiveTo", effectiveFrom)
                ).findList();
    }

    @Override
    public List<Delegation> findByIds(Collection<String> delegationIds) {
        return query().where().in("domainId", delegationIds).findList();
    }

    @Override
    public List<Delegation> findByDomainIds(Collection<String> domainIds) {
        return query().where().in("domainId", domainIds).findList();
    }
}
