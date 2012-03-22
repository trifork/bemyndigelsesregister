package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import dk.bemyndigelsesregister.bemyndigelsesservice.BemyndigelsesService;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ArbejdsfunktionDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.BemyndigelseDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.RettighedDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.StatusTypeDao;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.jws.WebParam;

@Repository
public class BemyndigelsesServiceImpl implements BemyndigelsesService {
    private static Logger logger = Logger.getLogger(BemyndigelsesServiceImpl.class);
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

    public BemyndigelsesServiceImpl() {
        logger.info("Starting Bemyndigelsesservice webservice");
    }

    @Override
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
