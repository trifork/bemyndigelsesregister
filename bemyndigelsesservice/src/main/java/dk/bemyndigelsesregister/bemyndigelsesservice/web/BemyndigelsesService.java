package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.StatusType;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ArbejdsfunktionDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.BemyndigelseDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.RettighedDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.StatusTypeDao;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class BemyndigelsesService {
    @Inject
    SystemService systemService;
    @Inject
    BemyndigelseDao bemyndigelseDao;
    @Inject
    ArbejdsfunktionDao arbejdsfunktionDao;
    @Inject
    StatusTypeDao statusTypeDao;
    @Inject
    RettighedDao rettighedDao;

    @WebMethod
    public String hello() {
        return "OK";
    }

    @WebMethod
    public void opretAnmodningOmBemyndigelse(String bemyndigedeCpr, String bemyndigedeCvr, String bemyndigendeCpr, long arbejdsfunktionId, long rettighedId) {
        final Bemyndigelse bemyndigelse = new Bemyndigelse();
        bemyndigelse.setBemyndigedeCpr(bemyndigedeCpr);
        bemyndigelse.setBemyndigedeCvr(bemyndigedeCvr);
        bemyndigelse.setBemyndigendeCpr(bemyndigendeCpr);
        bemyndigelse.setArbejdsfunktion(arbejdsfunktionDao.get(arbejdsfunktionId));
        bemyndigelse.setStatus(statusTypeDao.get(1));
        bemyndigelse.setRettighed(rettighedDao.get(rettighedId));
        bemyndigelse.setKode("KODE");
        bemyndigelse.setSystem("Trifork test system");
        bemyndigelse.setGyldigFra(systemService.getDateTime());
        bemyndigelse.setGyldigTil(systemService.getDateTime().plusYears(99));
        bemyndigelse.setVersionsid(1);

        bemyndigelseDao.save(bemyndigelse);
    }
}
