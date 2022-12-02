package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import org.joda.time.DateTime;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.File;

public interface SystemService {
    String getImplementationBuild();

    String getImplementationVersion();

    DateTime getDateTime();

    Result createXmlTransformResult();

    Source createXmlTransformSource(String unmarshalledObject);

    File writeToTempDir(String filename, String data);

    int cleanupTempDir(int retentionDays);

    String createUUIDString();
}
