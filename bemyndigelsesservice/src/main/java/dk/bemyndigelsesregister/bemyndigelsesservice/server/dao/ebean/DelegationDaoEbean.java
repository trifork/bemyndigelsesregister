package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationDao;
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
        return query().where().eq("bemyndigende_cpr", delegatorCpr).findList();
    }

    @Override
    public List<Delegation> findByDelegateeCpr(String delegateeCpr) {
        return query().where().eq("bemyndigede_cpr", delegateeCpr).findList();
    }

    @Override
    public List<Delegation> findByDomainIds(Collection<String> domainIds) {
        return query().where().in("kode", domainIds).findList();
    }
}
