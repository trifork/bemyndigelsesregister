package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.MessageRetransmission;

public interface MessageReplayDao {
    MessageRetransmission get(long id);

    void save(MessageRetransmission messageRetransmission);

    MessageRetransmission getByMessageIDAndImplementationBuild(String messageID, String implementationBuild);
}
