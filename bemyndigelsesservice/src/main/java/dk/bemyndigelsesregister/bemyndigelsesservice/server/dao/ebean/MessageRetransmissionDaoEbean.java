package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.MessageRetransmission;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.MessageRetransmissionDao;
import org.springframework.stereotype.Repository;

@Repository
public class MessageRetransmissionDaoEbean extends SupportDao<MessageRetransmission> implements MessageRetransmissionDao {
    public MessageRetransmissionDaoEbean() {
        super(MessageRetransmission.class);
    }

    @Override
    public MessageRetransmission getByMessageIDAndImplementationBuild(String messageID, String implementationBuild) {
        return query().where().eq("message_id", messageID).eq("implementation_build", implementationBuild).findUnique();
    }
}
