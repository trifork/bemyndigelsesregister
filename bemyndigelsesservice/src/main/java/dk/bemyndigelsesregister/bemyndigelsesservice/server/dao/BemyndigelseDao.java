package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;

public interface BemyndigelseDao {
    Bemyndigelse get(long id);

    void save(Bemyndigelse bemyndigelse);

    List<Bemyndigelse> findBySidstModificeretGreaterThanOrEquals(DateTime sidstModificeret);

    List<Bemyndigelse> list();

    Bemyndigelse findByKode(String kode);

    List<Bemyndigelse> findByBemyndigendeCpr(String bemyndigende);

    List<Bemyndigelse> findByBemyndigedeCpr(String bemyndigede);

    List<Bemyndigelse> findByKoder(Collection<String> bemyndigelsesKoder);

    List<Bemyndigelse> findByInPeriod(String bemyndigedeCpr, String bemyndigedeCvr, String arbejdsfunktionKode, String rettighedKode, String linkedSystemKode, DateTime gyldigFra, DateTime gyldigTil);
}
