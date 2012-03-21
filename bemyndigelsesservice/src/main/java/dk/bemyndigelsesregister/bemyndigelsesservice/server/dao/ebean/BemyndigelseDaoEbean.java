package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import com.avaje.ebean.EbeanServer;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.BemyndigelseDao;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository
public class BemyndigelseDaoEbean extends SupportDao<Bemyndigelse> implements BemyndigelseDao {
    @Inject
    EbeanServer ebeanServer;

    public BemyndigelseDaoEbean() {
        super(Bemyndigelse.class);
    }
}
