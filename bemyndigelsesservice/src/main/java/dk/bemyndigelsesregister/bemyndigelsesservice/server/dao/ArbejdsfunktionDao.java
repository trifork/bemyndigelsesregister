package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Domaene;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;

import java.util.List;

public interface ArbejdsfunktionDao {
    Arbejdsfunktion get(long id);

    void save(Arbejdsfunktion arbejdsfunktion);

    List<Arbejdsfunktion> findBy(Domaene domaene);

    Arbejdsfunktion findByKode(String kode);

    List<Arbejdsfunktion> findBy(Domaene domaene, LinkedSystem linkedSystem);
}
