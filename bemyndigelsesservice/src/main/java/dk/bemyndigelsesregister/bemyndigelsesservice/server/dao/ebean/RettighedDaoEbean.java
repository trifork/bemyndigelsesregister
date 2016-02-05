package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.RettighedDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RettighedDaoEbean extends SupportDao10<Rettighed> implements RettighedDao {

    public RettighedDaoEbean() {
        super(Rettighed.class);
    }

    @Override
    public Rettighed findByKode(LinkedSystem linkedSystem, String kode) {
        return query().where().eq("linkedSystem", linkedSystem).eq("kode", kode).findUnique();
    }

    @Override
    public List<Rettighed> findBy(LinkedSystem linkedSystem) {
        return query().where().eq("linkedSystem", linkedSystem).findList();
    }
}
