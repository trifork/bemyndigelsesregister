package dk.bemyndigelsesregister.ws;

import dk.bemyndigelsesregister.service.DumpRestoreManager;
import dk.ssi.nsi.xml_schema._2013._01._01.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.server.endpoint.annotation.SoapAction;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Endpoint
public class DumpRestoreWebService extends AbstractWebService implements DumpRestorePortType {
    private static final Logger log = LogManager.getLogger(DumpRestoreWebService.class);

    @Value("${dumprestore.enabled}")
    private boolean dumpRestoreEnabled;

    @Autowired
    private DumpRestoreManager dumpRestoreManager;

    @PostConstruct
    private void init() {
        log.info(dumpRestoreEnabled ? "Dump/restore enabled" : "Dump/restore disabled");
    }

    @Override
    @SoapAction("http://www.ssi.dk/nsi/xml.schema/2013/01/01#DumpPatients")
    @ResponsePayload
    public DumpPatientsResponse dumpPatients(@RequestPayload DumpPatientsRequest request) {
        log.warn("Unsupported operation DumpPatients invoked");
        throw new UnsupportedOperationException("Dump operation is not supported");
    }

    @Override
    @SoapAction("http://www.ssi.dk/nsi/xml.schema/2013/01/01#RestorePatients")
    @ResponsePayload
    public RestorePatientsResponse restorePatients(@RequestPayload RestorePatientsRequest request) {
        log.warn("Unsupported operation RestorePatients invoked");
        throw new UnsupportedOperationException("Restore operation is not supported");
    }

    @Override
    @SoapAction("http://www.ssi.dk/nsi/xml.schema/2013/01/01#ResetPatients")
    @ResponsePayload
    public ResetPatientsResponse resetPatients(@RequestPayload ResetPatientsRequest request) {
        try {
            if (!dumpRestoreEnabled) {
                throw new UnsupportedOperationException("Dump/restore is not enabled");
            }

            log.info("ResetPatients invoked");

            Set<String> identifiers = new HashSet<>();
            for (IdentifierType identifier : request.getIdentifiers()) {
                identifiers.addAll(identifier.getPersonIdentifiers());
            }

            List<String> result = dumpRestoreManager.resetPatients(new LinkedList<>(identifiers));

            ResetPatientsResponse response = new ResetPatientsResponse();
            List<IdentifierType> resultIdentifiers = response.getIdentifiers(); // ensure list in response is not null
            if (result != null) {
                for (String i : result) {
                    IdentifierType identifier = new IdentifierType();
                    identifier.getPersonIdentifiers().add(i);
                    resultIdentifiers.add(identifier);
                }
            }

            return response;
        } catch (UnsupportedOperationException e) {
            log.warn("ResetPatients invoked, but dump/restore is not enabled");
            throw e;
        } catch (Exception e) {
            log.error("Error in resetPatients", e);
            throw new RuntimeException(e);
        }
    }
}
