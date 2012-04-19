package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Domaene;

public interface DomaeneDao {
    Domaene get(long id);

    void save(Domaene domaene);
}
