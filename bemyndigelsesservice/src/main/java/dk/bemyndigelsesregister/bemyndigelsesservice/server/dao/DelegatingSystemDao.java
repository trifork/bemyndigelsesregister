package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by obj on 05-02-2016.
 */
public interface DelegatingSystemDao {
    DelegatingSystem get(long id);

    void save(DelegatingSystem delegatingSystem);

    DelegatingSystem findByCode(String code);

    List<DelegatingSystem> findByLastModifiedGreaterThanOrEquals(DateTime lastModified);
}
