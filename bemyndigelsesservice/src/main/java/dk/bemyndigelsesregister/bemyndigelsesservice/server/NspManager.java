package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import generated.BemyndigelserType;
import org.joda.time.DateTime;

public interface NspManager {
    void send(BemyndigelserType bemyndigelser, DateTime startTime);
}
