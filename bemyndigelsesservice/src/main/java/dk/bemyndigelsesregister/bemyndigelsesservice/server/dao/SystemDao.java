package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;

public interface SystemDao {
    DelegatingSystem get(long id);

    void save(DelegatingSystem delegatingSystem);

    DelegatingSystem findByCode(String code);
}
