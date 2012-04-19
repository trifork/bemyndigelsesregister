package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.MessageRetransmission;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.MessageReplayDao;
import org.junit.Test;

import javax.inject.Inject;
import java.util.UUID;

import static org.junit.Assert.*;

public class MessageReplayDaoEbeanTest extends DaoUnitTestSupport {

    @Inject
    MessageReplayDao dao;

    @Test
    public void willReturnNullOnNoRowsWithMessageID() throws Exception {
        assertNull(dao.getByMessageIDAndImplementationBuild(UUID.randomUUID().toString(), "V1"));
    }

    @Test
    public void canFindAReplayByMessageIDAndImplementationBuild() throws Exception {
        String messageID = UUID.randomUUID().toString();
        MessageRetransmission messageRetransmission = new MessageRetransmission(messageID, "TEST", "V2");
        dao.save(messageRetransmission);
        assertNotNull(messageRetransmission.getId());
        dao.save(new MessageRetransmission(messageID, "TEST", "V1"));

        MessageRetransmission foundMessageRetransmission = dao.getByMessageIDAndImplementationBuild(messageID, "V2");
        assertEquals(messageRetransmission.getMessageID(), foundMessageRetransmission.getMessageID());
        assertEquals(messageRetransmission.getMessageResponse(), foundMessageRetransmission.getMessageResponse());
        assertEquals(messageRetransmission.getImplementationBuild(), foundMessageRetransmission.getImplementationBuild());
    }
}
