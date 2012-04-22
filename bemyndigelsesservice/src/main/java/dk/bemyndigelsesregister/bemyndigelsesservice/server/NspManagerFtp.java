package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.shared.service.SystemService;
import generated.BemyndigelserType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;

@Repository
public class NspManagerFtp implements NspManager {
    @Inject
    SystemService systemService;

    @Inject
    Marshaller marshaller;

    @Value("${ftp.hostname}")
    String ftpHostname;

    @Value("${ftp.port}")
    int ftpPort;

    @Value("${ftp.username}")
    String ftpUsername;

    @Value("${ftp.password}")
    String ftpPassword;

    @Value("${ftp.remote}")
    String ftpRemote;

    @Override
    public void send(BemyndigelserType bemyndigelser, DateTime startTime) {
        final Result result = systemService.createXmlTransformResult();
        try {
            marshaller.marshal(bemyndigelser, result);

            File file = systemService.writeToTempDir(startTime.toString("yyyyMMdd'_'HHmmssSSS'_v1.bemyndigelse'"), result.toString());
            FTPClient ftpClient = new FTPClient();
            ftpClient.connect(ftpHostname, ftpPort);
            ftpClient.login(ftpUsername, ftpPassword);
            ftpClient.storeFile(ftpRemote, FileUtils.openInputStream(file));
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
