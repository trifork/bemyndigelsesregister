package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationPermissionDao;
import org.springframework.stereotype.Repository;

@Repository
public class DelegationPermissionDaoEbean extends SupportDao<DelegationPermission> implements DelegationPermissionDao {

    public DelegationPermissionDaoEbean() {
        super(DelegationPermission.class);
    }

    public void remove(DelegationPermission delegationPermission) {
        ebeanServer.delete(delegationPermission);
    }
}
