package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ArbejdsfunktionDao;
import org.springframework.stereotype.Repository;

@Repository
public class ArbejdsfunktionDaoEbean extends SupportDao<Arbejdsfunktion> implements ArbejdsfunktionDao {
    public ArbejdsfunktionDaoEbean() {
        super(Arbejdsfunktion.class);
    }
}
