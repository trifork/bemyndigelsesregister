package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;

public interface BemyndigelseDao {
    Bemyndigelse get(long id);

    void save(Bemyndigelse bemyndigelse);

    List<Bemyndigelse> findBySidstModificeretGreaterThan(DateTime sidstModificeret);

    List<Bemyndigelse> list();

    Bemyndigelse findByKode(String kode);

    Collection<Bemyndigelse> findByBemyndigendeCpr(String bemyndigende);

    Collection<Bemyndigelse> findByBemyndigedeCpr(String bemyndigede);

    Collection<Bemyndigelse> findByKoder(Collection<String> bemyndigelsesKoder);
}
