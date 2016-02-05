package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Permission;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationPermissionDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.PermissionDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DelegationPermissionDaoEbean extends SupportDao<DelegationPermission> implements DelegationPermissionDao {

    public DelegationPermissionDaoEbean() {
        super(DelegationPermission.class);
    }

    @Override
    public DelegationPermission findById(DelegatingSystem delegatingSystem, String id) {
        return query().where().eq("linkedSystem", delegatingSystem).eq("kode", id).findUnique();
    }

    @Override
    public List<DelegationPermission> findBy(DelegatingSystem delegatingSystem) {
        return query().where().eq("linkedSystem", delegatingSystem).findList();
    }
}
