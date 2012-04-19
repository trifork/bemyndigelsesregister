package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.MessageReplay;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.MessageReplayDao;
import org.springframework.stereotype.Repository;

@Repository
public class MessageReplayDaoEbean extends SupportDao<MessageReplay> implements MessageReplayDao {
    public MessageReplayDaoEbean() {
        super(MessageReplay.class);
    }

    @Override
    public MessageReplay getByMessageIDAndImplementationBuild(String messageID, String implementationBuild) {
        return query().where().eq("message_id", messageID).eq("implementation_build", implementationBuild).findUnique();
    }
}
