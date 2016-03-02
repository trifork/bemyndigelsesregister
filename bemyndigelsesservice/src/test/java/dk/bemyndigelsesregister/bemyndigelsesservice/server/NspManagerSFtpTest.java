package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.server.exportmodel.Delegations;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.oxm.Marshaller;

import javax.xml.transform.Result;
import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class NspManagerSFtpTest {
    private static final String TEST_USER = "test";
    private static final String TEST_PASSWORD = "Test1234";
    private static final int TEST_PORT = 22999;

    NspManagerSFtp nspManager = new NspManagerSFtp();
    SystemService systemService = mock(SystemService.class);
    Marshaller marshaller = mock(Marshaller.class);

    private final DateTime startTime = new DateTime(1982, 5, 21, 2, 15, 3);
    private final Delegations delegations = new Delegations();

    @Before
    public void setUp() {
        nspManager.systemService = systemService;
        nspManager.marshaller = marshaller;

        SFTPTestServer.startServer(TEST_USER, TEST_PASSWORD, TEST_PORT);

        nspManager.sftpClient = createSFtpClient();
    }

    @After
    public void destroy() {
        SFTPTestServer.destroy();
    }

    private SFtpClient createSFtpClient() {
        SFtpClientImpl sFtpClient = new SFtpClientImpl();
        sFtpClient.user = TEST_USER;
        sFtpClient.password = TEST_PASSWORD;
        sFtpClient.port = TEST_PORT;
        sFtpClient.hostName = "localhost";

        return sFtpClient;
    }

    @Test
    public void testSend() throws Exception {
        Result result = mock(Result.class);
        when(systemService.createXmlTransformResult()).thenReturn(result);
        final String fileBody = "File body";
        final File tempFile = File.createTempFile(TEST_USER, ".bemyndigelse");
        FileUtils.writeStringToFile(tempFile, fileBody);
        final String filename = "19820521_021503000_v001.bemyndigelse";
        delegations.setVersion("v001");

        when(result.toString()).thenReturn(fileBody);
        when(systemService.writeToTempDir(filename, fileBody)).thenReturn(tempFile);

        nspManager.send(delegations, startTime);
        //TODO: Check at filen findes "server side" efter upload
//        assertTrue(ftpServer.getFileSystem().exists("/" + filename));

//        assertEquals(fileBody, IOUtils.toString(((FileEntry) ftpServer.getFileSystem().getEntry("/" + filename)).createInputStream()));
    }
}
