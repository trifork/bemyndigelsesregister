package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Permission;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.PermissionDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PermissionDaoEbean extends SupportDao<Permission> implements PermissionDao {

    public PermissionDaoEbean() {
        super(Permission.class);
    }

    @Override
    public Permission findByDomainId(String delegatingSystemId, String id) {
        return query().where().eq("delegatingSystem.domainId", delegatingSystemId).eq("domainId", id).findUnique();
    }

    @Override
    public List<Permission> findBySystem(String delegatingSystemId) {
        return query().where().eq("delegatingSystem.domainId", delegatingSystemId).findList();
    }

    @Override
    public void remove(Permission permission) {
        ebeanServer.delete(permission);
    }
}
