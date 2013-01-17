package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Domaene;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed;

import java.util.List;

public interface RettighedDao {
    Rettighed get(long id);

    void save(Rettighed rettighed);

    Rettighed findByKode(LinkedSystem linkedSystem, String kode);

    List<Rettighed> findBy(LinkedSystem linkedSystem);
}
