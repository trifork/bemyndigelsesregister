package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse20;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Status;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.Bemyndigelse20Dao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * BEM 2.0 bemyndigelse
 * Created by obj on 02-02-2016.
 */
@Repository
public class Bemyndigelse20DaoEbean extends SupportDao<Bemyndigelse20> implements Bemyndigelse20Dao {
    protected Bemyndigelse20DaoEbean() {
        super(Bemyndigelse20.class);
    }

    @Override
    public List<Bemyndigelse20> findByBemyndigendeCpr(String bemyndigendeCpr) {
        return query().where().eq("bemyndigende_cpr", bemyndigendeCpr).findList();
    }

    @Override
    public List<Bemyndigelse20> findByBemyndigedeCpr(String bemyndigedeCpr) {
        return query().where().eq("bemyndigede_cpr", bemyndigedeCpr).findList();
    }

    @Override
    public Bemyndigelse20 findByKode(String bemyndigelsesKode) {
        return query().where().eq("kode", bemyndigelsesKode).findUnique();
    }

    @Override
    public List<Bemyndigelse20> findByInPeriod(String system, String delegatorCpr, String delegateeCpr, String delegateeCvr, String role, Status state, DateTime effectiveFrom, DateTime effectiveTo) {
        return query().where()
                .eq("linkedSystemKode", system)
                .eq("bemyndigendeCpr", delegatorCpr)
                .eq("bemyndigedeCpr", delegateeCpr)
                .eq("bemyndigedeCvr", delegateeCvr)
                .eq("arbejdsfunktionKode", role)
                .eq("status", state).and(
                        expr().le("gyldigFra", effectiveTo),
                        expr().gt("gyldigTil", effectiveFrom)
                ).findList();
    }

    @Override
    public List<Bemyndigelse20> findByKoder(Collection<String> bemyndigelsesKoder) {
        return query().where().in("kode", bemyndigelsesKoder).findList();
    }
}
