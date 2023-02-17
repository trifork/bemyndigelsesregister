package dk.bemyndigelsesregister.batch;

import org.springframework.core.io.Resource;

/**
 *
 */
public interface SFtpClient {
    void upload(Resource resource, String remoteDir);
}
