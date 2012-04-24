package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.nsi.bemyndigelser._2012._04.Bemyndigelser;
import org.joda.time.DateTime;

public interface NspManager {
    void send(Bemyndigelser bemyndigelser, DateTime startTime);
}
