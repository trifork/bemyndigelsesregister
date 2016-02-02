package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse20;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;

/**
 * BEM 2.0 bemyndigelse
 * Created by obj on 02-02-2016.
 */
public interface Bemyndigelse20Manager {
    Bemyndigelse20 opretAnmodningOmBemyndigelse(String linkedSystemKode, String bemyndigendeCpr, String bemyndigedeCpr, String bemyndigedeCvr, String arbejdsfunktionKode, List<String> rettighedKoder, String systemKode, DateTime gyldigFra, DateTime gyldigTil);

    Collection<Bemyndigelse20> godkendBemyndigelser(Collection<String> bemyndigelsesKoder);

    Bemyndigelse20 opretGodkendtBemyndigelse(String linkedSystemKode, String bemyndigendeCpr, String bemyndigedeCpr, String bemyndigedeCvr, String arbejdsfunktionKode, List<String> rettighedKoder, String systemKode, DateTime gyldigFra, DateTime gyldigTil);
}
