package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse20;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Status;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;

/**
 * BEM 2.0 bemyndigelse
 * Created by obj on 02-02-2016.
 */
public interface Bemyndigelse20Dao {
    Bemyndigelse20 get(long id);

    void save(Bemyndigelse20 bemyndigelse);

    List<Bemyndigelse20> list();

    List<Bemyndigelse20> findByBemyndigendeCpr(String bemyndigende);

    List<Bemyndigelse20> findByBemyndigedeCpr(String bemyndigede);

    Bemyndigelse20 findByKode(String bemyndigelsesKode);

    List<Bemyndigelse20> findByInPeriod(String system, String delegatorCpr, String delegateeCpr, String delegateeCvr, String role, Status state, DateTime effectiveFrom, DateTime effectiveTo);

    List<Bemyndigelse20> findByKoder(Collection<String> bemyndigelsesKoder);
}
