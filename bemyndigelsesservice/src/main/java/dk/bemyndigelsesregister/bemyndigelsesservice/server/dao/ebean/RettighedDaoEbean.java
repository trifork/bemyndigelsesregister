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
    public Rettighed findByRettighedskode(String rettighedskode) {
        return query().where().eq("rettighedskode", rettighedskode).findUnique();
    }
}
