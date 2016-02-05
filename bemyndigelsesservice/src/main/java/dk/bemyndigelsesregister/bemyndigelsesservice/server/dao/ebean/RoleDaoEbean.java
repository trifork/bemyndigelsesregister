package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
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
    public Role findByDomainId(String delegatingSystemid, String id) {
        return query().where().eq("linkedSystem", delegatingSystemid).eq("kode", id).findUnique();
    }

    @Override
    public List<Role> findBySystem(String delegatingSystemId) {
        return query().where().eq("linkedSystem", delegatingSystemId).findList();
    }
}
