package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import org.joda.time.DateTime;

import java.util.Collection;

public interface BemyndigelseManager {
    Bemyndigelse opretAnmodningOmBemyndigelse(String bemyndigendeCpr, String bemyndigedeCpr, String bemyndigedeCvr, String arbejdsfunktionKode, String rettighedKode, String systemKode, DateTime gyldigFra, DateTime gyldigTil);

    Collection<Bemyndigelse> godkendBemyndigelser(Collection<String> bemyndigelsesKoder);

    Bemyndigelse opretGodkendtBemyndigelse(String bemyndigendeCpr, String bemyndigedeCpr, String bemyndigedeCvr, String arbejdsfunktionKode, String rettighedKode, String systemKode, DateTime gyldigFra, DateTime gyldigTil);
}
