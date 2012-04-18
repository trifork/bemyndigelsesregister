package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import com.trifork.dgws.MedcomReplay;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.MessageReplay;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.MessageReplayDao;
import org.junit.Before;
import org.junit.Test;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

import javax.xml.transform.Source;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MedcomReplayRegisterImplTest {
    private final MessageReplayDao messageReplayDao = mock(MessageReplayDao.class);
    private final MedcomReplayRegisterImpl register = new MedcomReplayRegisterImpl();
    private final Marshaller marshaller = mock(Marshaller.class);
    private final Unmarshaller unmarshaller = mock(Unmarshaller.class);

    @Before
    public void setUp() throws Exception {
        register.messageReplayDao = messageReplayDao;
        register.marshaller = marshaller;
        register.unmarshaller = unmarshaller;
    }

    @Test
    public void willReturnNullOnNoReplay() throws Exception {
        assertNull(register.getReplay("TEST"));
    }

    @Test
    public void willReturnMappedMedcomReplay() throws Exception {
        String unmarshalledObject = "UnmarshalledObject";
        String messageID = "TEST";

        when(messageReplayDao.getByMessageID(messageID)).thenReturn(new MessageReplay(messageID, new byte[0]));
        when(unmarshaller.unmarshal(any(Source.class))).thenReturn(unmarshalledObject);

        MedcomReplay medcomReplay = register.getReplay(messageID);

        assertEquals(messageID, medcomReplay.getMessageId());
        assertEquals(unmarshalledObject, medcomReplay.getResponseMessage());
    }
}
