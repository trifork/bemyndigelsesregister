package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Domain;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DomainDao;
import org.springframework.stereotype.Repository;

@Repository
public class DomainDaoEbean extends SupportDao<Domain> implements DomainDao {
    public DomainDaoEbean() {
        super(Domain.class);
    }
}
