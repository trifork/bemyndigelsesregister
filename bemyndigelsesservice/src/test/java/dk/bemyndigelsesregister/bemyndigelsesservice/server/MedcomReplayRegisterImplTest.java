package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import com.trifork.dgws.MedcomReplay;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.MessageRetransmission;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.MessageRetransmissionDao;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MedcomReplayRegisterImplTest {
    private final MessageRetransmissionDao messageRetransmissionDao = mock(MessageRetransmissionDao.class);
    private final MedcomReplayRegisterImpl register = new MedcomReplayRegisterImpl();
    private final Marshaller marshaller = mock(Marshaller.class);
    private final Unmarshaller unmarshaller = mock(Unmarshaller.class);
    private final SystemService systemService = mock(SystemService.class);

    private final Object unmarshalledObject = "UnmarshalledObject";
    private final String marshalledObject = "MarshalledObject";
    private final String messageID = "MessageID";
    private String implementationBuild = "V1";

    @Before
    public void setUp() throws Exception {
        register.messageRetransmissionDao = messageRetransmissionDao;
        register.marshaller = marshaller;
        register.unmarshaller = unmarshaller;
        register.systemService = systemService;
    }

    @Test
    public void willReturnNullOnNoReplay() throws Exception {
        assertNull(register.getReplay("TEST"));
    }

    @Test
    public void willReturnMappedMedcomReplay() throws Exception {
        Source source = mock(Source.class);

        when(messageRetransmissionDao.getByMessageIDAndImplementationBuild(messageID, null)).thenReturn(new MessageRetransmission(messageID, marshalledObject, implementationBuild));
        when(systemService.createXmlTransformSource(marshalledObject)).thenReturn(source);
        when(unmarshaller.unmarshal(source)).thenReturn(unmarshalledObject);

        MedcomReplay medcomReplay = register.getReplay(messageID);

        assertEquals(messageID, medcomReplay.getMessageId());
        assertEquals(unmarshalledObject, medcomReplay.getResponseMessage());
    }

    @Test
    public void willNotReturnMappedMedcomRetransmissionWhenVersionHasChanged() throws Exception {
        Source source = mock(Source.class);

        when(systemService.getImplementationBuild()).thenReturn(implementationBuild);
        when(messageRetransmissionDao.getByMessageIDAndImplementationBuild(messageID, implementationBuild)).thenReturn(new MessageRetransmission(messageID, marshalledObject, implementationBuild));
        when(systemService.createXmlTransformSource(marshalledObject)).thenReturn(source);
        when(unmarshaller.unmarshal(source)).thenReturn(unmarshalledObject);

        MedcomReplay medcomReplay = register.getReplay(messageID);

        verify(messageRetransmissionDao).getByMessageIDAndImplementationBuild(messageID, implementationBuild);

        assertEquals(unmarshalledObject, medcomReplay.getResponseMessage());
    }

    @Test
    public void canSaveReplay() throws Exception {
        Result result = mock(Result.class);

        when(systemService.createXmlTransformResult()).thenReturn(result);
        when(result.toString()).thenReturn(marshalledObject);

        register.createReplay(messageID, unmarshalledObject);

        verify(messageRetransmissionDao).save(argThat(new TypeSafeMatcher<MessageRetransmission>() {
            @Override
            public boolean matchesSafely(MessageRetransmission item) {
                return item.getMessageID().equals(messageID) && item.getMessageResponse().equals(marshalledObject);
            }
            @Override
            public void describeTo(Description description) {

            }
        }));
        verify(marshaller).marshal(unmarshalledObject, result);
    }
}
