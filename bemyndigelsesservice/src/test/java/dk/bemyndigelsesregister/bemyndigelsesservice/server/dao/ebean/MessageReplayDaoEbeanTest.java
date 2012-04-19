package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.MessageReplay;
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
        MessageReplay messageReplay = new MessageReplay(messageID, "TEST", "V2");
        dao.save(messageReplay);
        assertNotNull(messageReplay.getId());
        dao.save(new MessageReplay(messageID, "TEST", "V1"));

        MessageReplay foundMessageReplay = dao.getByMessageIDAndImplementationBuild(messageID, "V2");
        assertEquals(messageReplay.getMessageID(), foundMessageReplay.getMessageID());
        assertEquals(messageReplay.getMessageResponse(), foundMessageReplay.getMessageResponse());
        assertEquals(messageReplay.getImplementationBuild(), foundMessageReplay.getImplementationBuild());
    }
}
