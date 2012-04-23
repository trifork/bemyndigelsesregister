package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.BemyndigelseDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BemyndigelseDaoEbean extends SupportDao<Bemyndigelse> implements BemyndigelseDao {
    public BemyndigelseDaoEbean() {
        super(Bemyndigelse.class);
    }

    @Override
    public List<Bemyndigelse> findBySidstModificeretGreaterThan(DateTime dateTime) {
        return null;
    }
}
