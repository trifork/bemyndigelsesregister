package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse20;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.Bemyndigelse20Dao;
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
    public List<Bemyndigelse20> findByKoder(Collection<String> bemyndigelsesKoder) {
        return query().where().in("kode", bemyndigelsesKoder).findList();
    }
}
