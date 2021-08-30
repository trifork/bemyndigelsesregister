package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegatingSystemDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by obj on 05-02-2016.
 */
@Repository
public class DelegatingSystemDaoEBean extends SupportDao<DelegatingSystem> implements DelegatingSystemDao {
    protected DelegatingSystemDaoEBean() {
        super(DelegatingSystem.class);
    }

    @Override
    public List<DelegatingSystem> findAll() {
        return query().findList();
    }

    @Override
    public List<DelegatingSystem> findByLastModifiedGreaterThanOrEquals(DateTime lastModified) {
        return query().where().ge("lastModified", lastModified).findList();
    }
}
