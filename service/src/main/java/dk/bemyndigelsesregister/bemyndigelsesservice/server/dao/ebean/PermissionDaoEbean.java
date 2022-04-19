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
    public Permission findByCode(String systemCode, String code) {
        return query().where().eq("system.code", systemCode).eq("code", code).findUnique();
    }

    @Override
    public List<Permission> findBySystem(Long systemId) {
        return query().where().eq("system.id", systemId).findList();
    }

    @Override
    public void remove(Permission permission) {
        ebeanServer.delete(permission);
    }
}
