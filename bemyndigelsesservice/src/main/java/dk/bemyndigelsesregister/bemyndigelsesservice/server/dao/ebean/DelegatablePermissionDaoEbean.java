package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatablePermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegerbarRettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationPermissionDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegerbarRettighedDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DelegatablePermissionDaoEbean extends SupportDao<DelegatablePermission> implements DelegatablePermissionDao {
    public DelegatablePermissionDaoEbean() {
        super(DelegatablePermission.class);
    }

    @Override
    public List<DelegatablePermission> findBy(LinkedSystem linkedSystem) {
        return query().fetch("arbejdsfunktion").where().eq("arbejdsfunktion.linkedSystem", linkedSystem).findList();
    }
}
