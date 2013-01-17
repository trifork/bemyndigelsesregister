package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Domaene;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;

import java.util.List;

public interface ArbejdsfunktionDao {
    Arbejdsfunktion get(long id);

    void save(Arbejdsfunktion arbejdsfunktion);

    Arbejdsfunktion findByKode(LinkedSystem linkedSystem, String kode);

    List<Arbejdsfunktion> findBy(LinkedSystem linkedSystem);
}
