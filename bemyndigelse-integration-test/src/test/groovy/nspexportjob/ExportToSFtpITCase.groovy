package nspexportjob

import dk.bemyndigelsesregister.bemyndigelsesservice.server.SFTPTestServer
import org.apache.commons.io.IOUtils
import org.junit.After
import org.junit.Before
import org.junit.Test

import static dk.bemyndigelsesregister.integrationtest.TestUtil.urlPrefix
import static groovy.util.GroovyTestCase.assertEquals

class ExportToSFtpITCase {

    final String TEST_USER = "test";
    final String TEST_PASSWORD = "Test1234";
    final int TEST_PORT = 22999;

    @Before
    public void setUp() {
        SFTPTestServer.startServer(TEST_USER, TEST_PASSWORD, TEST_PORT);

//        ftpServer.addUserAccount(new UserAccount("bemyndigelse", "BEMYNDIGELSE", "/"))

/*        FileSystem fileSystem = new UnixFakeFileSystem()
        fileSystem.add(new DirectoryEntry("/"))
        ftpServer.fileSystem = fileSystem

        ftpServer.start();
*/
    }

    @Test
    public void completeJobWillExportAllBemyndigelser() {
        Date startTime = new Date()

        URLConnection urlConnection = new URL(urlPrefix() + "/op/export").openConnection()
        assertEquals "DONE", IOUtils.toString(urlConnection.inputStream)

        //TODO: verificer at filen findes "server side" efter upload
/*        FileEntry uploadedFile = ftpServer.fileSystem.listFiles("/").find { FileSystemEntry entry ->
            entry.lastModified.after(startTime)
        }
        assert uploadedFile != null, 'Ingen uploaded file fundet på ftp'
        def bemyndiglser = new XmlParser().parse(uploadedFile.createInputStream())

        println "bemyndiglser = $bemyndiglser"
        assert bemyndiglser.'@AntalPost' >= 2
        assert bemyndiglser.'@AntalPost'.toInteger() == bemyndiglser.'Bemyndigelse'.size()
        assert bemyndiglser.'Bemyndigelse'.find { it.'bemyndigende_cpr'.text() == '1010101010' }
*/
    }

    @After
    public void tearDown() throws Exception {
        SFTPTestServer.destroy();
    }

}
