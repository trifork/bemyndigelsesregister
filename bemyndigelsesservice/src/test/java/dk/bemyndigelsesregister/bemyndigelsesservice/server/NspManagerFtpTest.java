package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.shared.service.SystemService;
import generated.BemyndigelserType;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.springframework.oxm.Marshaller;

import javax.xml.transform.Result;

import static org.mockito.Mockito.*;

public class NspManagerFtpTest {
    NspManagerFtp nspManagerFtp = new NspManagerFtp();
    SystemService systemService = mock(SystemService.class);
    Marshaller marshaller = mock(Marshaller.class);

    private final DateTime startTime = new DateTime();
    private final BemyndigelserType bemyndigelser = new BemyndigelserType();
    private FakeFtpServer ftpServer;

    @Before
    public void setUp() throws Exception {
        ftpServer = new FakeFtpServer();
        ftpServer.setServerControlPort(2121);
        ftpServer.addUserAccount(new UserAccount("nsp", "password", "/"));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/"));
        ftpServer.setFileSystem(fileSystem);

        ftpServer.start();

        nspManagerFtp.systemService = systemService;
        nspManagerFtp.marshaller = marshaller;

        nspManagerFtp.ftpUsername = "nsp";
        nspManagerFtp.ftpPassword = "password";
        nspManagerFtp.ftpPort = 2121;
    }

    @Test
    public void canUploadToFtp() throws Exception {
        Result result = mock(Result.class);
        when(systemService.createXmlTransformResult()).thenReturn(result);
        nspManagerFtp.send(bemyndigelser, startTime);

    }

    @After
    public void tearDown() throws Exception {
        ftpServer.stop();
    }
}
