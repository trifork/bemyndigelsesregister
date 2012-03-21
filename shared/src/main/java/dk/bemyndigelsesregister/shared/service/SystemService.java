package dk.bemyndigelsesregister.shared.service;

import org.joda.time.DateTime;

import java.util.Date;

public interface SystemService {
    Date getDate();

    String getImplementationBuild();

    DateTime getDateTime();
}
