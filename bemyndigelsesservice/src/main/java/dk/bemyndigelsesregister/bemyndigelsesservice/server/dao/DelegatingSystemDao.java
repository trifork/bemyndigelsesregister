package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;

/**
 * Created by obj on 05-02-2016.
 */
public interface DelegatingSystemDao {
    DelegatingSystem get(long id);

    void save(DelegatingSystem delegatingSystem);

    DelegatingSystem findByCode(String code);
}
