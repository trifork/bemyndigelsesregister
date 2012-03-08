package dk.bemyndigelsesregister.shared.service;

import java.util.Date;

public interface SystemService {
    Date getDate();

    String getImplementationBuild();
}
