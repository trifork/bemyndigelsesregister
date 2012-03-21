package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.StatusType;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.StatusTypeDao;
import org.springframework.stereotype.Repository;

@Repository
public class StatusTypeDaoEbean extends SupportDao<StatusType> implements StatusTypeDao {
    public StatusTypeDaoEbean() {
        super(StatusType.class);
    }
}
