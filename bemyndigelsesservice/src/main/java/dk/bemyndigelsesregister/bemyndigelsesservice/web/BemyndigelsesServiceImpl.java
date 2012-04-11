package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import dk.bemyndigelsesregister.bemyndigelsesservice.BemyndigelsesService;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@Repository("bemyndigelsesServiceServer")
@WebService(serviceName = "bemyndigelsesservice.svc")
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
    @Inject
    LinkedSystemDao linkedSystemDao;

    public BemyndigelsesServiceImpl() {
        logger.info("Starting Bemyndigelsesservice webservice");
    }

    @Override
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
        bemyndigelse.setLinkedSystem(linkedSystemDao.get(1l));
        bemyndigelse.setGyldigFra(systemService.getDateTime());
        bemyndigelse.setGyldigTil(systemService.getDateTime().plusYears(99));
        bemyndigelse.setVersionsid(1);

        bemyndigelseDao.save(bemyndigelse);
    }
}
