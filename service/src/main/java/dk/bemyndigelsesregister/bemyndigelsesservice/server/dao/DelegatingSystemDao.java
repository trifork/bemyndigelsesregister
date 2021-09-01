package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import org.joda.time.DateTime;

import java.util.List;

public interface DelegatingSystemDao {
    DelegatingSystem get(long id);

    void save(DelegatingSystem delegatingSystem);

    DelegatingSystem findByCode(String code);

    List<DelegatingSystem> findAll();

    List<DelegatingSystem> findByLastModifiedGreaterThanOrEquals(DateTime lastModified);
}
