package dk.bemyndigelsesregister.shared.service;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.jar.Manifest;

@Service
public class SystemServiceDefault implements SystemService {
    private static Logger logger = Logger.getLogger(SystemServiceDefault.class);

    @Value("${temp.dir}")
    String tempDirLocation;

    @Inject
    ServletContext servletContext;

    @Override
    public DateTime getDateTime() {
        return new DateTime();
    }

    @Override
    public String getImplementationBuild() {
        Manifest manifest = getManifest();
        if (manifest == null) {
            return "develop";
        }
        final String implementationBuild = manifest.getMainAttributes().getValue("Implementation-Build");
        if (implementationBuild == null) {
            logger.warn("No Implementation-Build property found in Manifest file");
            return "develop";
        }
        return implementationBuild;
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

    /**
     * Creates a Result equivalent to Spring-xml org.springframework.xml.transform.StringResult
     *
     * @return Slightly modified StreamReader
     */
    @Override
    public StreamResult createXmlTransformResult() {
        return new StreamResult(new StringWriter()) {
            @Override
            public String toString() {
                return getWriter().toString();
            }
        };
    }

    @Override
    public Source createXmlTransformSource(final String unmarshalledObject) {
        return new StreamSource() {
            private final String content;

            {
                content = unmarshalledObject;
            }

            @Override
            public Reader getReader() {
                return new StringReader(content);
            }

            @Override
            public String toString() {
                return content;
            }
        };
    }

    @Override
    public File writeToTempDir(String filename, String data) {
        final File file = new File(tempDirLocation, filename);
        try {
            FileUtils.writeStringToFile(file, data);
        } catch (IOException e) {
            throw new RuntimeException("Could not write to file=" + file.getAbsolutePath(), e);
        }
        return file;
    }
}
