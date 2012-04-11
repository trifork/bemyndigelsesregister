package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;

public interface BemyndigelseDao {
    Bemyndigelse get(long id);

    void save(Bemyndigelse bemyndigelse);
}
