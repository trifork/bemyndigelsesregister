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
    public List<DelegerbarRettighed> findBy(LinkedSystem linkedSystem) {
        return query().fetch("arbejdsfunktion").where().eq("arbejdsfunktion.linkedSystem", linkedSystem).findList();
    }
}
