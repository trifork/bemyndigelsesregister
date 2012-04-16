package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import dk.bemyndigelsesregister.bemyndigelsesservice.BemyndigelsesService;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.response.OpretAnmodningOmBemyndigelseResponse;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.request.OpretAnmodningOmBemyndigelseRequest;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.inject.Inject;

@Repository("bemyndigelsesServiceServer")
@Endpoint
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
    public @ResponsePayload OpretAnmodningOmBemyndigelseResponse opretAnmodningOmBemyndigelse(@RequestPayload OpretAnmodningOmBemyndigelseRequest request) {
        final Bemyndigelse bemyndigelse = new Bemyndigelse();
        bemyndigelse.setBemyndigedeCpr(request.getBemyndigedeCpr());
        bemyndigelse.setBemyndigedeCvr(request.getBemyndigedeCvr());
        bemyndigelse.setBemyndigendeCpr(request.getBemyndigendeCpr());
        bemyndigelse.setArbejdsfunktion(arbejdsfunktionDao.get(request.getArbejdsfunktionId()));
        bemyndigelse.setStatus(statusTypeDao.get(1));
        bemyndigelse.setRettighed(rettighedDao.get(request.getRettighedId()));
        bemyndigelse.setKode("KODE");
        bemyndigelse.setLinkedSystem(linkedSystemDao.get(1l));
        bemyndigelse.setGyldigFra(systemService.getDateTime());
        bemyndigelse.setGyldigTil(systemService.getDateTime().plusYears(99));
        bemyndigelse.setVersionsid(1);

        bemyndigelseDao.save(bemyndigelse);
        return new OpretAnmodningOmBemyndigelseResponse();
    }
}
