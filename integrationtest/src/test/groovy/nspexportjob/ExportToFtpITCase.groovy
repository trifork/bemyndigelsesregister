package nspexportjob

import org.apache.commons.io.IOUtils
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockftpserver.fake.FakeFtpServer
import org.mockftpserver.fake.UserAccount
import org.mockftpserver.fake.filesystem.DirectoryEntry
import org.mockftpserver.fake.filesystem.FileEntry
import org.mockftpserver.fake.filesystem.FileSystem
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem

import static dk.bemyndigelsesregister.integrationtest.TestUtil.urlPrefix
import static groovy.util.GroovyTestCase.assertEquals
import org.mockftpserver.fake.filesystem.FileSystemEntry
@Ignore("NspManagerSftp is being injected in BemyndigelseEXportJob so FTP will not be used")
class ExportToFtpITCase {
    final FakeFtpServer ftpServer = new FakeFtpServer()

    @Before
    public void setUp() {
        ftpServer.serverControlPort = 21213
        ftpServer.addUserAccount(new UserAccount("bemyndigelse", "BEMYNDIGELSE", "/"))

        FileSystem fileSystem = new UnixFakeFileSystem()
        fileSystem.add(new DirectoryEntry("/"))
        ftpServer.fileSystem = fileSystem

        ftpServer.start();
    }

    @Test
    public void completeJobWillExportAllBemyndigelser() {
        Date startTime = new Date()

        URLConnection urlConnection = new URL(urlPrefix() + "/op/export").openConnection()
        assertEquals "DONE", IOUtils.toString(urlConnection.inputStream)

        FileEntry uploadedFile = ftpServer.fileSystem.listFiles("/").find {FileSystemEntry entry ->
            entry.lastModified.after(startTime)
        }
        assert uploadedFile != null, 'Ingen uploaded file fundet på ftp'
        def bemyndiglser = new XmlParser().parse(uploadedFile.createInputStream())

        println "bemyndiglser = $bemyndiglser"
        assert bemyndiglser.'@AntalPost' >= 2
        assert bemyndiglser.'@AntalPost'.toInteger() == bemyndiglser.'Bemyndigelse'.size()
        assert bemyndiglser.'Bemyndigelse'.find {it.'bemyndigende_cpr'.text() == '1010101010'}

    }

    @After
    public void tearDown() throws Exception {
        ftpServer.stop();
    }

}
