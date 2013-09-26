package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.BemyndigelseDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class BemyndigelseDaoEbean extends SupportDao<Bemyndigelse> implements BemyndigelseDao {
    public BemyndigelseDaoEbean() {
        super(Bemyndigelse.class);
    }

    @Override
    public List<Bemyndigelse> findBySidstModificeretGreaterThanOrEquals(DateTime sidstModificeret) {
        return query().where().ge("sidst_modificeret", sidstModificeret).findList();
    }

    @Override
    public List<Bemyndigelse> findByKoder(Collection<String> bemyndigelsesKoder) {
        return query().where().in("kode", bemyndigelsesKoder).findList();
    }

    @Override
    public List<Bemyndigelse> findByInPeriod(String bemyndigedeCpr, String bemyndigedeCvr, String arbejdsfunktionKode, String rettighedKode, String linkedSystemKode, DateTime gyldigFra, DateTime gyldigTil) {
        return query().where()
                .eq("bemyndigede_cpr", bemyndigedeCpr)
                .eq("bemyndigede_cvr", bemyndigedeCvr)
                .eq("arbejdsfunktionKode", arbejdsfunktionKode)
                .eq("rettighedKode", rettighedKode)
                .eq("linkedSystemKode", linkedSystemKode)
                .and(
                        expr().between("gyldigFra", gyldigFra, gyldigTil),
                        expr().between("gyldigTil", gyldigFra, gyldigTil)
                )
                .findList();
    }

    @Override
    public List<Bemyndigelse> findByBemyndigendeCpr(String bemyndigendeCpr) {
        return query().where().eq("bemyndigende_cpr", bemyndigendeCpr).findList();
    }

    @Override
    public List<Bemyndigelse> findByBemyndigedeCpr(String bemyndigedeCpr) {
        return query().where().eq("bemyndigede_cpr", bemyndigedeCpr).findList();
    }
}
