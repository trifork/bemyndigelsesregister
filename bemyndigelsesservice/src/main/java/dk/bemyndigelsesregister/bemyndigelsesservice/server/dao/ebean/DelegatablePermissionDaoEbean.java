package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatablePermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegatablePermissionDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DelegatablePermissionDaoEbean extends SupportDao<DelegatablePermission> implements DelegatablePermissionDao {
    public DelegatablePermissionDaoEbean() {
        super(DelegatablePermission.class);
    }

    @Override
    public DelegatablePermission findByPermissionAndRole(String permissionId, String roleId) {
        return query().where().eq("permission.domainId", permissionId).eq("role.domainId", roleId).findUnique();
    }

    @Override
    public List<DelegatablePermission> findBySystem(String delegatingSystemId) {
        return query().where().eq("role.delegatingSystem.domainId", delegatingSystemId).findList();
    }

    @Override
    public void remove(DelegatablePermission delegatablePermission) {
        ebeanServer.delete(delegatablePermission);
    }
}

