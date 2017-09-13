package dk.bemyndigelsesregister.shared.service;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.UUID;
import java.util.jar.Manifest;

@Service
public class SystemServiceDefault implements SystemService {
    private static Logger logger = Logger.getLogger(SystemServiceDefault.class);

    private static String implementationBuild = null;
    private static String implementationVersion = null;

    @Value("${temp.dir}")
    String tempDirLocation;

    @Inject
    ServletContext servletContext;

    @Override
    public DateTime getDateTime() {
        return new DateTime(DateTimeZone.UTC);
    }

    @Override
    public String getImplementationBuild() {
        if (implementationBuild == null) {
            implementationBuild = getManifestProperty("Implementation-Build");
        }
        return implementationBuild;
    }

    @Override
    public String getImplementationVersion() {
        if (implementationVersion == null) {
            implementationVersion = getManifestProperty("Implementation-Version");
        }
        return implementationVersion;
    }

    private String getManifestProperty(String name) {
        Manifest manifest = getManifest();
        if (manifest != null) {
            String value = manifest.getMainAttributes().getValue(name);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        logger.warn("Property " + name + " not found in Manifest file");
        return "develop";
    }

    private Manifest getManifest() {
        try {
            final InputStream resourceAsStream = servletContext.getResourceAsStream("META-INF/MANIFEST.MF");
            if (resourceAsStream == null) {
                logger.warn("No manifest file found");
                return null;
            }
            return new Manifest(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException("Could not open manifest", e);
        }
    }

    /**
     * Creates a Result equivalent to Spring-xml org.springframework.xml.transform.StringResult
     *
     * @return Slightly modified StreamReader
     */
    @Override
    public Result createXmlTransformResult() {
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
            FileUtils.writeStringToFile(file, data, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Could not write to file=" + file.getAbsolutePath(), e);
        }
        return file;
    }

    @Override
    public String createUUIDString() {
        return UUID.randomUUID().toString();
    }
}
