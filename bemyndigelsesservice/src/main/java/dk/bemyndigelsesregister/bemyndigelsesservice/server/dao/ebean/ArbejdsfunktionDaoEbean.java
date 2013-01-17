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
    public Arbejdsfunktion findByKode(LinkedSystem linkedSystem, String kode) {
        return query().where().eq("linkedSystem", linkedSystem).eq("kode", kode).findUnique();
    }

    @Override
    public List<Arbejdsfunktion> findBy(LinkedSystem linkedSystem) {
        return query().where().eq("linkedSystem", linkedSystem).findList();
    }
}
