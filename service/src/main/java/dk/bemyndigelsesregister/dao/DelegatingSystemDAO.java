package dk.bemyndigelsesregister.dao;

import dk.bemyndigelsesregister.domain.DelegatingSystem;
import java.time.Instant;

import java.util.List;

public interface DelegatingSystemDAO {
    DelegatingSystem get(long id);

    void save(DelegatingSystem delegatingSystem);

    DelegatingSystem findByCode(String code);

    List<DelegatingSystem> findByDomain(long domainId);

    List<DelegatingSystem> findByLastModifiedGreaterThanOrEquals(Instant lastModified);
}
