package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.RettighedDao;
import org.springframework.stereotype.Repository;

@Repository
public class RettighedDaoEbean extends SupportDao<Rettighed> implements RettighedDao {

    public RettighedDaoEbean() {
        super(Rettighed.class);
    }

    @Override
    public Rettighed findByKode(String rettighedskode) {
        //TODO:
        return null;
    }
}
