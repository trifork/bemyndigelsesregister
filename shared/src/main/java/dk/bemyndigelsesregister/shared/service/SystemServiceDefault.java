package dk.bemyndigelsesregister.shared.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.jar.Manifest;

@Service
public class SystemServiceDefault implements SystemService {
    private final Log logger = LogFactory.getLog(getClass());
    @Inject
    ServletContext servletContext;

    @Override
    public Date getDate() {
        return new Date();
    }

    @Override
    public String getImplementationBuild() {
        Manifest manifest = getManifest();
        if (manifest == null) {
            return "develop";
        }
        return manifest.getMainAttributes().getValue("Implementation-Build");
    }

    private static Manifest manifest;

    public Manifest getManifest() {
        if (manifest == null) {
            try {
                final InputStream resourceAsStream = servletContext.getResourceAsStream("META-INF/MANIFEST.MF");
                if (resourceAsStream == null) {
                    logger.warn("No manifest file found");
                    return null;
                }
                manifest = new Manifest(resourceAsStream);
            } catch (IOException e) {
                throw new RuntimeException("Could not open manifest", e);
            }
        }
        return manifest;
    }

}
