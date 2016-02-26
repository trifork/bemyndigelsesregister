package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.bemyndigelse._2016._01._01.Delegation;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.transform.Result;
import java.io.File;
import java.util.List;

import static org.springframework.util.Assert.notNull;

/**
 *
 */
@Repository("nspManagerSftp")
public class NspManagerSFtp implements NspManager {
    private static final Logger logger = Logger.getLogger(NspManagerSFtp.class);

    @Inject
    SFtpClient sftpClient;

    @Inject
    SystemService systemService;

    @Inject
    @Named("nspMarshaller")
    Marshaller marshaller;

    @Value("${sftp.remote.path}")
    String remoteFolder = "";

    @Value("${sftp.enabled}")
    boolean exportEnabled = true;


    @PostConstruct
    public void init() throws Exception {
        notNull(remoteFolder, "No sftp remote has been set");
    }


    @Override
    public void send(List<Delegation> delegations, DateTime startTime) {
        final Result result = systemService.createXmlTransformResult();
        try {
            marshaller.marshal(delegations, result);

//            final String filename = startTime.toString("yyyyMMdd'_'HHmmssSSS'_" + bemyndigelser.getVersion() + ".bemyndigelse'");
            final String filename = startTime.toString("yyyyMMdd'_'HHmmssSSS'_v001.bemyndigelse'"); // TODO OBJ v001 is temporarily hardcoded as version
            File file = systemService.writeToTempDir(filename, result.toString());
            logger.debug("Sending " + file.getAbsolutePath() + " with name " + filename);
            if (exportEnabled) {
                sftpClient.upload(new FileSystemResource(file), remoteFolder);
            } else {
                logger.info("SFTP export has been disabled. Was supposed to send file=" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send file", e);
        }
    }

}
