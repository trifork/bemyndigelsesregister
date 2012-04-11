package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.LinkedSystemDao;
import org.springframework.stereotype.Repository;

@Repository
public class LinkedSystemDaoEbean extends SupportDao<LinkedSystem> implements LinkedSystemDao {

    public LinkedSystemDaoEbean() {
        super(LinkedSystem.class);
    }
}
