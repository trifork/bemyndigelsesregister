package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.MessageRetransmission;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.MessageReplayDao;
import org.springframework.stereotype.Repository;

@Repository
public class MessageReplayDaoEbean extends SupportDao<MessageRetransmission> implements MessageReplayDao {
    public MessageReplayDaoEbean() {
        super(MessageRetransmission.class);
    }

    @Override
    public MessageRetransmission getByMessageIDAndImplementationBuild(String messageID, String implementationBuild) {
        return query().where().eq("message_id", messageID).eq("implementation_build", implementationBuild).findUnique();
    }
}
