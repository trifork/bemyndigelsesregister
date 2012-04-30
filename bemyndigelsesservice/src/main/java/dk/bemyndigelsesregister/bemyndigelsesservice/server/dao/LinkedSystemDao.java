package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;

public interface LinkedSystemDao {
    LinkedSystem get(long id);

    void save(LinkedSystem linkedSystem);

    LinkedSystem findBySystem(String linkedSystemKode);
}
