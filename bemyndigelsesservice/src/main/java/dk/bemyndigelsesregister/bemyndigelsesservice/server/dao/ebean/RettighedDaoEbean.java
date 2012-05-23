package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Domaene;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.RettighedDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RettighedDaoEbean extends SupportDao<Rettighed> implements RettighedDao {

    public RettighedDaoEbean() {
        super(Rettighed.class);
    }

    @Override
    public List<Rettighed> findBy(Domaene domaene, LinkedSystem linkedSystem) {
        return query().where().eq("domaene", domaene).eq("linkedSystem", linkedSystem).findList();
    }
}
