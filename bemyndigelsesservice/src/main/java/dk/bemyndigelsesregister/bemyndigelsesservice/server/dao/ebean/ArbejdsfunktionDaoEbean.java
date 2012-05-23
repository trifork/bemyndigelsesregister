package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Domaene;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ArbejdsfunktionDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ArbejdsfunktionDaoEbean extends SupportDao<Arbejdsfunktion> implements ArbejdsfunktionDao {
    public ArbejdsfunktionDaoEbean() {
        super(Arbejdsfunktion.class);
    }

    @Override
    public List<Arbejdsfunktion> findBy(Domaene domaene) {
        return query().where().eq("domaene", domaene).findList();
    }

    @Override
    public List<Arbejdsfunktion> findBy(Domaene domaene, LinkedSystem linkedSystem) {
        return query().where().eq("domaene", domaene).eq("linked_system", linkedSystem).findList();
    }
}
