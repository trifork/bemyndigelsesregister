package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.server.exportmodel.Delegations;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;

import static org.springframework.util.Assert.hasText;

@Repository
public class NspManagerFtp implements NspManager, InitializingBean {
    private static final Logger logger = Logger.getLogger(NspManagerFtp.class);
    @Inject
    SystemService systemService;

    @Inject
    @Named("nspMarshaller")
    Marshaller marshaller;

    @Value("${ftp.hostname}")
    String ftpHostname = "localhost";

    @Value("${ftp.port}")
    int ftpPort = 21;

    @Value("${ftp.username}")
    String ftpUsername;

    @Value("${ftp.password}")
    String ftpPassword;

    @Value("${ftp.remote}")
    String ftpRemote = "/";

    @Value("${ftp.enabled}")
    boolean exportEnabled = true;

    @Override
    public void afterPropertiesSet() throws Exception {
        hasText(ftpHostname, "No ftp hostname has been set");
        if (ftpPort < 1) {
            logger.warn("ftp server port (ftp.port) is less than 1, actually '" + ftpPort + "'. Using default ftp port, 21");
            ftpPort = 21;
        }
        hasText(ftpUsername, "No ftp username has been set");
        hasText(ftpPassword, "No ftp password has been set");
        hasText(ftpRemote, "No ftp remote has been set");
    }

    @Override
    public void send(Delegations delegations, DateTime startTime, int batchNo) {
        final Result result = systemService.createXmlTransformResult();
        FTPClient ftpClient = new FTPClient();
        try {
            marshaller.marshal(delegations, result);

            final String filename = startTime.toString("yyyyMMdd'_'HHmmssSSS'_'") + delegations.getVersion() + "_" + String.format("%03d", batchNo) + ".bemyndigelse";
            File file = systemService.writeToTempDir(filename, result.toString());
            logger.debug("Sending " + file.getAbsolutePath() + " with name " + filename);
            if (exportEnabled) {
                logger.info(String.format("Connecting to ftp://%s@%s:%s%s", ftpUsername, ftpHostname, ftpPort, ftpRemote));
                ftpClient.connect(ftpHostname, ftpPort);
                ftpClient.login(ftpUsername, ftpPassword);
                ftpClient.storeFile(ftpRemote + filename, FileUtils.openInputStream(file));
            } else {
                logger.info("FTP export has been disabled. Was supposed to send file=" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send file", e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                logger.error("Could not disconnect from FTP server");
            }
        }
    }
}
