package dk.bemyndigelsesregister.shared.service;

import org.joda.time.DateTime;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.File;

public interface SystemService {
    String getImplementationBuild();

    DateTime getDateTime();

    Result createXmlTransformResult();

    Source createXmlTransformSource(String unmarshalledObject);

    File writeToTempDir(String filename, String data);
}
