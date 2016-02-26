package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.nsi.bemyndigelse._2016._01._01.Delegation;
import org.joda.time.DateTime;

import java.util.List;

public interface NspManager {
    void send(List<Delegation> delegations, DateTime startTime);
}
