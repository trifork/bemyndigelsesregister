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
        assertNull(dao.getByMessageID(UUID.randomUUID().toString()));
    }

    @Test
    public void canFindAReplayByMessageID() throws Exception {
        String messageID = UUID.randomUUID().toString();
        MessageReplay messageReplay = new MessageReplay(messageID, "TEST");
        dao.save(messageReplay);
        assertNotNull(messageReplay.getId());

        MessageReplay foundMessageReplay = dao.getByMessageID(messageID);
        assertEquals(messageReplay.getMessageID(), foundMessageReplay.getMessageID());
        assertEquals(messageReplay.getMessageResponse(), foundMessageReplay.getMessageResponse());
    }
}
