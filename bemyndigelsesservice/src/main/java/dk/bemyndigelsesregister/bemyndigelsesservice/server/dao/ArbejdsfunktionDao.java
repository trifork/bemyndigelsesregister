package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion;
import org.springframework.stereotype.Repository;

public interface ArbejdsfunktionDao {
    Arbejdsfunktion get(long id);

    void save(Arbejdsfunktion arbejdsfunktion);
}
