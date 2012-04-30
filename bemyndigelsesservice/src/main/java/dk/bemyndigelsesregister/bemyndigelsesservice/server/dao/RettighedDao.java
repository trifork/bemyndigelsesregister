package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed;

public interface RettighedDao {
    Rettighed get(long id);

    void save(Rettighed rettighed);

    Rettighed findByKode(String rettighedskode);
}
