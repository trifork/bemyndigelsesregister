package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.PermissionDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PermissionDaoEbean extends SupportDao<Permission> implements PermissionDao {

    public PermissionDaoEbean() {
        super(Permission.class);
    }

    @Override
    public Permission findById(DelegatingSystem delegatingSystem, String id) {
        return query().where().eq("linkedSystem", delegatingSystem).eq("kode", id).findUnique();
    }

    @Override
    public List<Permission> findBy(DelegatingSystem delegatingSystem) {
        return query().where().eq("linkedSystem", delegatingSystem).findList();
    }
}
