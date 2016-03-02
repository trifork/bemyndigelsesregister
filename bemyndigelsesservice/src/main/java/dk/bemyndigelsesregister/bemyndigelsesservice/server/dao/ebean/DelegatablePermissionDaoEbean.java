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
    public DelegatablePermission findByPermissionAndRole(Long permissionId, Long roleId) {
        return query().where().eq("permission.id", permissionId).eq("role.id", roleId).findUnique();
    }

    @Override
    public List<DelegatablePermission> findBySystem(Long systemId) {
        return query().where().eq("role.system.id", systemId).findList();
    }

    @Override
    public void remove(DelegatablePermission delegatablePermission) {
        ebeanServer.delete(delegatablePermission);
    }
}

