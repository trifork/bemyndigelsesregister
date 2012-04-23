package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import com.trifork.dgws.MedcomRetransmission;
import com.trifork.dgws.MedcomRetransmissionRegister;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.MessageRetransmission;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.MessageRetransmissionDao;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.apache.log4j.Logger;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.xml.transform.Result;
import java.io.IOException;

@Repository
public class MedcomRetransmissionRegisterImpl implements MedcomRetransmissionRegister {
    private static final Logger logger = Logger.getLogger(MedcomRetransmissionRegisterImpl.class);

    @Inject
    Marshaller marshaller;

    @Inject
    Unmarshaller unmarshaller;

    @Inject
    MessageRetransmissionDao messageRetransmissionDao;

    @Inject
    SystemService systemService;

    @Override
    public MedcomRetransmission getReplay(String messageID) {
        //TODO: check for implementationbuild
        MessageRetransmission messageRetransmission = messageRetransmissionDao.getByMessageIDAndImplementationBuild(messageID, systemService.getImplementationBuild());
        if (messageRetransmission == null) {
            logger.debug("Found no MessageRetransmission for messageID=" + messageID);
            return null;
        }

        Object responseMessage;
        try {
            responseMessage = unmarshaller.unmarshal(systemService.createXmlTransformSource(messageRetransmission.getMessageResponse()));
        } catch (IOException e) {
            throw new RuntimeException("Could not unmarshal responseMessage", e);
        }

        return new MedcomRetransmission(messageRetransmission.getMessageID(), responseMessage);
    }

    @Override
    public void createReplay(String messageID, Object responseMessage) {
        Result result = systemService.createXmlTransformResult();
        try {
            marshaller.marshal(responseMessage, result);
        } catch (IOException e) {
            throw new RuntimeException("Could not marshal responseMessage", e);
        }

        MessageRetransmission messageRetransmission = new MessageRetransmission(messageID, result.toString(), systemService.getImplementationBuild());

        messageRetransmissionDao.save(messageRetransmission);
    }
}
