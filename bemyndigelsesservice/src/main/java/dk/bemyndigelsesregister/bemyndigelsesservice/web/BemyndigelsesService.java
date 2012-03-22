package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ArbejdsfunktionDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.BemyndigelseDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.RettighedDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.StatusTypeDao;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName = "bemyndigelsesservice.svc")
public class BemyndigelsesService {
    private static Logger logger = Logger.getLogger(BemyndigelsesService.class);
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

    public BemyndigelsesService() {
        logger.info("Starting Bemyndigelsesservice webservice");
    }

    @WebMethod
    public void opretAnmodningOmBemyndigelse(
            @WebParam(name = "bemyndigedeCpr") String bemyndigedeCpr,
            @WebParam(name = "bemyndigedeCvr") String bemyndigedeCvr,
            @WebParam(name = "bemyndigendeCpr") String bemyndigendeCpr,
            @WebParam(name = "arbejdsfunktionId") long arbejdsfunktionId,
            @WebParam(name = "rettighedId") long rettighedId) {
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
