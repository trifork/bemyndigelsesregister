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
    public List<DelegatablePermission> findBySystem(String delegatingSystemId) {
        return query().fetch("arbejdsfunktion").where().eq("arbejdsfunktion.linkedSystem", delegatingSystemId).findList();
    }
}
