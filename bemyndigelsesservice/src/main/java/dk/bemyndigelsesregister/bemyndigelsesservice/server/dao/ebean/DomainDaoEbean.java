package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Domain;
import org.springframework.stereotype.Repository;

@Repository
public class DomainDaoEbean extends SupportDao<Domain> implements DomainDao {
    public DomainDaoEbean() {
        super(Domain.class);
    }

    @Override
    public void save(Domain domain) {

    }

    @Override
    public Domain findById(String domainId) {
        return null;
    }
}
