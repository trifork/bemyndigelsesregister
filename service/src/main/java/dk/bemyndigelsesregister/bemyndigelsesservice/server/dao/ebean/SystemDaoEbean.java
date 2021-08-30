package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.SystemDao;
import org.springframework.stereotype.Repository;

@Repository
public class SystemDaoEbean extends SupportDao<DelegatingSystem> implements SystemDao {

    public SystemDaoEbean() {
        super(DelegatingSystem.class);
    }
}
