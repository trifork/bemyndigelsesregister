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
    public Role findById(String systemid, String id) {
        return query().where().eq("linkedSystem", systemid).eq("kode", id).findUnique();
    }

    @Override
    public List<Role> findBySystem(System system) {
        return query().where().eq("linkedSystem", system).findList();
    }
}
