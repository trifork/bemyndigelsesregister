package dk.bemyndigelsesregister.batch;

import com.jcraft.jsch.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;

import static org.springframework.util.Assert.hasText;

/**
 *
 */
@Repository("sftpClient")
public class SFtpClientImpl implements SFtpClient {
    private static final Logger logger = LogManager.getLogger(SFtpClientImpl.class);

    private static final JSch jsch = new JSch();
    static {
        JSch.setLogger(new Log4jJSchLogger());
    }

    @Value("${sftp.hostname}")
    String hostName = "localhost";

    @Value("${sftp.port}")
    int port = 22;

    @Value("${sftp.username}")
    String user;

    @Value("${sftp.password}")
    String password;

    @Value("${sftp.knownhosts}")
    Resource knownHosts;

    /**
     * Initialize knownhosts and make sure configureJSch hook is called so subclasses can provide further configuration
     */
    @PostConstruct
    public void init() {
        hasText(hostName, "No sftp hostname has been set");
        if (port < 1) {
            logger.warn("sftp server port (sftp.port) is less than 1, actually '" + port + "'. Using default sftp port, 22");
            port = 22;
        }
        hasText(user, "No sftp username has been set");
        hasText(password, "No sftp password has been set");

        if (knownHosts != null && knownHosts.exists()) {
            try {
                jsch.setKnownHosts(knownHosts.getFile().getAbsolutePath());
            } catch (Exception e) {
                logger.error("Failed setting known hosts file", e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void upload(Resource resource, String remoteDir) {
        if (remoteDir == null) remoteDir = "";
        try {
            put(resource.getFile(), remoteDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("Done uploading resource");

    }

    /**
     * Create connection, put one file to remote server in remoteDir location, then disconnect
     * @param srcFile
     * @param remoteDir
     */
    private void put(File srcFile, String remoteDir) {
        ChannelSftp c = null;
        Channel channel = null;
        Session session = null;
        try {
            session = getSession();
            channel = session.openChannel("sftp");
            channel.connect();
            c = (ChannelSftp) channel;

            String fsrc = srcFile.getAbsolutePath();
            String fdest = remoteDir;
            //Current version of JSch (0.1.49) requires both source and destination to be file paths
            if (remoteDir.endsWith("/") || remoteDir.trim().length() == 0) {
                fdest = fdest + srcFile.getName();
            } else {
                fdest = fdest + "/" + srcFile.getName();
            }

            logger.info("Starting file upload of file '" + srcFile.getName() + "' to remote file path '" + fdest + "'");
            c.put(fsrc, fdest);
            logger.info("Uploaded file '" + srcFile.getName() + "' successfully");

        } catch (Exception e) {
            logger.error("Upload of " + srcFile.getAbsolutePath() + " failed", e);
            throw new RuntimeException("Upload of " + srcFile.getAbsolutePath() + " failed", e);
        } finally {
            if (c != null) {
                c.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }

    }

    private Session getSession() throws JSchException {
        Session session = jsch.getSession(user, hostName, port);

        if (password != null) {
            session.setPassword(password);
        }

        session.setDaemonThread(true);

        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        return session;
    }


    private static class Log4jJSchLogger implements com.jcraft.jsch.Logger {
        @Override
        public boolean isEnabled(int level) {
            return true;
        }

        @Override
        public void log(int level, String message) {
            System.out.println(message);
            if (level == com.jcraft.jsch.Logger.DEBUG) {
                logger.debug(message);
            } else if (level == com.jcraft.jsch.Logger.INFO) {
                logger.debug(message);
            } else if (level == com.jcraft.jsch.Logger.WARN) {
                logger.warn(message);
            } else if (level == com.jcraft.jsch.Logger.ERROR) {
                logger.error(message);
            } else if (level == com.jcraft.jsch.Logger.FATAL) {
                logger.fatal(message);
            } else {
                logger.warn("Unknown com.jcraft.jsch.Logger log level '" + level + "' - Message: " + message);
            }
        }
    }
}
