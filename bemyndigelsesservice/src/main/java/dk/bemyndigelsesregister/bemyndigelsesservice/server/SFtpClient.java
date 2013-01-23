package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import org.springframework.core.io.Resource;

/**
 *
 */
public interface SFtpClient {

    void upload(Resource resource, String remoteDir);
}
