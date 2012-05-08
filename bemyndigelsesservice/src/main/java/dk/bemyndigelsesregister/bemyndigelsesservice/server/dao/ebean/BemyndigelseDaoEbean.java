package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
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
    public List<Bemyndigelse> findBySidstModificeretGreaterThan(DateTime sidstModificeret) {
        return query().where().gt("sidst_modificeret", sidstModificeret).findList();
    }

    @Override
    public Bemyndigelse findByKode(String kode) {
        return query().where().eq("kode", kode).findUnique();
    }

    @Override
    public Collection<Bemyndigelse> findByKoder(Collection<String> bemyndigelsesKoder) {
        return query().where().in("kode", bemyndigelsesKoder).findList();
    }

    @Override
    public Collection<Bemyndigelse> findByBemyndigendeCpr(String bemyndigendeCpr) {
        return query().where().eq("bemyndigende_cpr", bemyndigendeCpr).findList();
    }

    @Override
    public Collection<Bemyndigelse> findByBemyndigedeCpr(String bemyndigedeCpr) {
        return query().where().eq("bemyndigede_cpr", bemyndigedeCpr).findList();
    }
}
