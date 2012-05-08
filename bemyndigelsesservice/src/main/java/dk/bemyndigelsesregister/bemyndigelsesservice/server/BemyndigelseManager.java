package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;

import java.util.Collection;

public interface BemyndigelseManager {
    Bemyndigelse opretAnmodningOmBemyndigelse(String bemyndigendeCpr, String bemyndigedeCpr, String bemyndigedeCvr, String arbejdsfunktionKode, String rettighedKode, String systemKode);

    Collection<Bemyndigelse> godkendBemyndigelser(Collection<String> bemyndigelsesKoder);
}
