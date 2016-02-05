package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationPermissionDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DelegationPermissionDaoEbean extends SupportDao<DelegationPermission> implements DelegationPermissionDao {

    public DelegationPermissionDaoEbean() {
        super(DelegationPermission.class);
    }

    @Override
    public List<DelegationPermission> findByDelegation(Delegation delegation) {
        return query().where().eq("bemyndigelse", delegation).findList();
    }
}
