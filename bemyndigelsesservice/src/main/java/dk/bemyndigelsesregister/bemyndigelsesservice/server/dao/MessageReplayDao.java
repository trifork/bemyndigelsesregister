package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.MessageReplay;

public interface MessageReplayDao {
    MessageReplay get(long id);

    void save(MessageReplay messageReplay);

    MessageReplay getByMessageID(String messageID);
}
