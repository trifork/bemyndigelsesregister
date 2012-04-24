package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.bemyndigelser._2012._04.Bemyndigelser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.springframework.oxm.Marshaller;

import javax.xml.transform.Result;

import java.io.File;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NspManagerFtpTest {
    NspManagerFtp nspManagerFtp = new NspManagerFtp();
    SystemService systemService = mock(SystemService.class);
    Marshaller marshaller = mock(Marshaller.class);

    private final DateTime startTime = new DateTime(1982, 5, 21, 2, 15, 3);
    private final Bemyndigelser bemyndigelser = new Bemyndigelser() {{
        version = "v001";
    }};
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
        final String fileBody = "File body";
        final File tempFile = File.createTempFile("test", ".bemyndigelse");
        FileUtils.writeStringToFile(tempFile, fileBody);
        final String filename = "19820521_021503000_v001.bemyndigelse";

        when(result.toString()).thenReturn(fileBody);
        when(systemService.writeToTempDir(filename, fileBody)).thenReturn(tempFile);

        nspManagerFtp.send(bemyndigelser, startTime);

        assertTrue(ftpServer.getFileSystem().exists("/" + filename));

        assertEquals(fileBody, IOUtils.toString(((FileEntry) ftpServer.getFileSystem().getEntry("/" + filename)).createInputStream()));
    }

    @After
    public void tearDown() throws Exception {
        ftpServer.stop();
    }
}
