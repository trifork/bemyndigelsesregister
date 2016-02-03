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
    public List<Delegation> findByBemyndigendeCpr(String bemyndigendeCpr) {
        return query().where().eq("bemyndigende_cpr", bemyndigendeCpr).findList();
    }

    @Override
    public List<Delegation> findByBemyndigedeCpr(String bemyndigedeCpr) {
        return query().where().eq("bemyndigede_cpr", bemyndigedeCpr).findList();
    }

    @Override
    public List<Delegation> findByKoder(Collection<String> bemyndigelsesKoder) {
        return query().where().in("kode", bemyndigelsesKoder).findList();
    }
}
