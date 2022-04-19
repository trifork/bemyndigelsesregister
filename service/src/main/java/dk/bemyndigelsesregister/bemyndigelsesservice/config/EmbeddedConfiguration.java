package dk.bemyndigelsesregister.bemyndigelsesservice.config;

import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("embedded")
public class EmbeddedConfiguration {
    @Bean(initMethod = "start")
    public FakeFtpServer nspFtp(@Value("${ftp.port}") int port, @Value("${ftp.username}") String username, @Value("${ftp.password}") String password) {
        FakeFtpServer server = new FakeFtpServer();
        server.setServerControlPort(port);
        server.addUserAccount(new UserAccount(username, password, "/"));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/"));
        server.setFileSystem(fileSystem);

        return server;
    }
}
