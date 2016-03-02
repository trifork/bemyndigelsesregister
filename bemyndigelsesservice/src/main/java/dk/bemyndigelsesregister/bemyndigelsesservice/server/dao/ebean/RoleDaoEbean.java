package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Role;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.RoleDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleDaoEbean extends SupportDao<Role> implements RoleDao {
    public RoleDaoEbean() {
        super(Role.class);
    }

    @Override
    public Role findByCode(Long systemId, String code) {
        return query().where().eq("system.id", systemId).eq("code", code).findUnique();
    }

    @Override
    public List<Role> findBySystem(Long systemId) {
        return query().where().eq("system.id", systemId).findList();
    }

    @Override
    public void remove(Role role) {
        ebeanServer.delete(role);
    }
}
