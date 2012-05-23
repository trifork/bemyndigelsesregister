package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegerbarRettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Domaene;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegerbarRettighedDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DelegerbarRettighedDaoEbean extends SupportDao<DelegerbarRettighed> implements DelegerbarRettighedDao {
    public DelegerbarRettighedDaoEbean() {
        super(DelegerbarRettighed.class);
    }

    @Override
    public List<DelegerbarRettighed> findBy(Domaene domaene, LinkedSystem linkedSystem) {
        return query().where().eq("domaene", domaene).eq("linkedSystem", linkedSystem).findList();
    }
}
