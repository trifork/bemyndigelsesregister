package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.server.exportmodel.Delegations;
import org.joda.time.DateTime;

public interface NspManager {
    void send(Delegations delegations, DateTime startTime, int batchNo);
}
