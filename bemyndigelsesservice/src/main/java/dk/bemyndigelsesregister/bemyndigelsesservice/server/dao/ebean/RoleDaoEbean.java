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
    public Role findByDomainId(Long delegatingSystemid, String id) {
        return query().where().eq("delegatingSystem.id", delegatingSystemid).eq("domainId", id).findUnique();
    }

    @Override
    public List<Role> findBySystem(Long delegatingSystemId) {
        return query().where().eq("delegatingSystem.id", delegatingSystemId).findList();
    }
}
