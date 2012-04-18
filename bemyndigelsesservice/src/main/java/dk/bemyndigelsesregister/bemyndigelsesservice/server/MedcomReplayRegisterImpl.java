package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import com.trifork.dgws.MedcomReplay;
import com.trifork.dgws.MedcomReplayRegister;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.MessageReplay;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.MessageReplayDao;
import org.apache.log4j.Logger;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Repository;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import javax.inject.Inject;
import java.io.IOException;

@Repository
public class MedcomReplayRegisterImpl implements MedcomReplayRegister {
    private static final Logger logger = Logger.getLogger(MedcomReplayRegisterImpl.class);

    @Inject
    Marshaller marshaller;

    @Inject
    Unmarshaller unmarshaller;

    @Inject
    MessageReplayDao messageReplayDao;

    @Override
    public MedcomReplay getReplay(String messageID) {
        MessageReplay messageReplay = messageReplayDao.getByMessageID(messageID);
        if (messageReplay == null) {
            logger.debug("Found no MessageReplay for messageID=" + messageID);
            return null;
        }

        Object responseMessage;
        try {
            responseMessage = unmarshaller.unmarshal(new StringSource(new String(messageReplay.getMessageResponse())));
        } catch (IOException e) {
            throw new RuntimeException("Could not unmarshal responseMessage", e);
        }

        return new MedcomReplay(messageReplay.getMessageID(), responseMessage);
    }

    @Override
    public void createReplay(String messageID, Object responseMessage) {
        StringResult result = new StringResult();
        try {
            marshaller.marshal(responseMessage, result);
        } catch (IOException e) {
            throw new RuntimeException("Could not marshal responseMessage", e);
        }

        MessageReplay messageReplay = new MessageReplay(messageID, result.toString().getBytes());

        messageReplayDao.save(messageReplay);
    }
}
