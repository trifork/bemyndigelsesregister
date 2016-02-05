package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Domaene;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DomaeneDao;
import org.springframework.stereotype.Repository;

@Repository
public class DomaeneDaoEbean extends SupportDao10<Domaene> implements DomaeneDao {
    public DomaeneDaoEbean() {
        super(Domaene.class);
    }
}
