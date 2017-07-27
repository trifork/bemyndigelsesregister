package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.MessageRetransmission;
import org.joda.time.DateTime;

public interface MessageRetransmissionDao {
    MessageRetransmission get(long id);

    void save(MessageRetransmission messageRetransmission);

    MessageRetransmission getByMessageIDAndImplementationBuild(String messageID, String implementationBuild);

    int cleanup(DateTime beforeDate, int maxRecords);
}
