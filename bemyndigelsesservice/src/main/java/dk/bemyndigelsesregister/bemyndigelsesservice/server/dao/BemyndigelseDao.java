package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import org.joda.time.DateTime;

import java.util.List;

public interface BemyndigelseDao {
    Bemyndigelse get(long id);

    void save(Bemyndigelse bemyndigelse);

    List<Bemyndigelse> findBySidstModificeretGreaterThan(DateTime dateTime);
}
