package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegatingSystemDao;
import org.springframework.stereotype.Repository;

/**
 * Created by obj on 05-02-2016.
 */
@Repository
public class DelegatingSystemDaoEBean extends SupportDao<DelegatingSystem> implements DelegatingSystemDao {
    protected DelegatingSystemDaoEBean() {
        super(DelegatingSystem.class);
    }
}
