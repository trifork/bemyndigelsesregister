package dk.bemyndigelsesregister.shared.service;

import org.joda.time.DateTime;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.util.Date;

public interface SystemService {
    Date getDate();

    String getImplementationBuild();

    DateTime getDateTime();

    Result createXmlTransformResult();

    Source createXmlTransformSource(String unmarshalledObject);
}
