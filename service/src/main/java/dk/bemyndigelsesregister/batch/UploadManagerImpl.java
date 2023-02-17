package dk.bemyndigelsesregister.batch;

import dk.bemyndigelsesregister.batch.exportmodel.Delegations;
import dk.bemyndigelsesregister.service.SystemService;
import dk.bemyndigelsesregister.util.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import java.io.File;
import java.time.Instant;

import static org.apache.commons.lang.Validate.notNull;

/**
 *
 */
@Component
public class UploadManagerImpl implements UploadManager {
    private static final Logger logger = LogManager.getLogger(UploadManagerImpl.class);

    @Autowired
    private SFtpClient sftpClient;

    @Autowired
    private SystemService systemService;

    @Value("${sftp.remote.path}")
    private String remoteFolder = "";

    @Value("${sftp.enabled}")
    boolean exportEnabled = true;

    private JAXBContext jaxbContext;

    @PostConstruct
    public void init() throws JAXBException {
        notNull(remoteFolder, "No sftp remote has been set");

        jaxbContext = JAXBContext.newInstance(Delegations.class);
    }

    @Override
    public void upload(Delegations delegations, Instant startTime, int batchNo) {
        final Result result = systemService.createXmlTransformResult();
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(delegations, result);

            final String filename = DateUtils.format(startTime, "yyyyMMdd_HHmmssSSS_") + delegations.getVersion() + "_" + String.format("%03d", batchNo) + ".bemyndigelse";
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
