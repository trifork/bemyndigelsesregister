package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.trifork.dgws.annotations.Protected;
import dk.bemyndigelsesregister.bemyndigelsesservice.BemyndigelsesService;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import com.trifork.dgws.util.SecurityHelper;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.request.OpretAnmodningOmBemyndigelseRequest;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.request.SletBemyndigelserRequest;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.response.OpretAnmodningOmBemyndigelseResponse;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.response.SletBemyndigelserResponse;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeader;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Repository("bemyndigelsesService")
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
    @Inject
    Unmarshaller unmarshaller;
    @Inject
    SecurityHelper securityHelper;

    public BemyndigelsesServiceImpl() {
    }

    @Override
    @Protected(whitelist = "BemyndigelsesService.opretAnmodningOmBemyndigelser")
    @Transactional
    public @ResponsePayload OpretAnmodningOmBemyndigelseResponse opretAnmodningOmBemyndigelser(
            @RequestPayload OpretAnmodningOmBemyndigelseRequest request, SoapHeader soapHeader) {
        final Bemyndigelse bemyndigelse = new Bemyndigelse();
        bemyndigelse.setBemyndigedeCpr(request.getBemyndigedeCpr());
        bemyndigelse.setBemyndigedeCvr(request.getBemyndigedeCvr());
        bemyndigelse.setBemyndigendeCpr(request.getBemyndigendeCpr());
        bemyndigelse.setArbejdsfunktion(arbejdsfunktionDao.findByArbejdsfunktion(request.getArbejdsfunktion()));
        bemyndigelse.setStatus(statusTypeDao.get(1));
        bemyndigelse.setRettighed(rettighedDao.findByRettighedskode(request.getRettighed()));
        bemyndigelse.setKode(systemService.createUUIDString());
        bemyndigelse.setLinkedSystem(linkedSystemDao.findBySystem(request.getLinkedSystem()));
        final DateTime now = systemService.getDateTime();
        bemyndigelse.setGyldigFra(now);
        bemyndigelse.setGyldigTil(now.plusYears(99));
        bemyndigelse.setVersionsid(1);

        bemyndigelseDao.save(bemyndigelse);
        return new OpretAnmodningOmBemyndigelseResponse();
    }

    @Override
    @Protected(whitelist = "BemyndigelsesService.sletBemyndigelser")
    @Transactional
    public @ResponsePayload SletBemyndigelserResponse sletBemyndigelser(
            @RequestPayload SletBemyndigelserRequest request, SoapHeader soapHeader) {

        String cpr = securityHelper.getCpr(soapHeader);

        List<String> deletedBemyndigelser = new ArrayList<String>();

        DateTime now = systemService.getDateTime();

        for (String kode : request.getBemyndigelsesKoder()) {
            Bemyndigelse bemyndigelse = bemyndigelseDao.findByKode(kode);

            if (!bemyndigelse.getBemyndigendeCpr().equals(cpr)) {
                logger.error("User has different Cpr=" + cpr + " than BemyndigendeCpr=" + bemyndigelse.getBemyndigendeCpr());
                throw new IllegalAccessError("User has different CPR than BemyndigedeCpr for kode=" + bemyndigelse.getKode());
            }

            DateTime validTo = bemyndigelse.getGyldigTil();
            if (validTo.isAfter(now)) {
                logger.info("Deleting bemyndigelse with id=" + bemyndigelse.getId() + " and kode=" + bemyndigelse.getKode());
                bemyndigelse.setGyldigTil(now);
                bemyndigelseDao.save(bemyndigelse);
                deletedBemyndigelser.add(bemyndigelse.getKode());
            }
            else {
                logger.info("Bemyndigelse with id=" + bemyndigelse.getId() + " and kode=" + bemyndigelse.getKode() + " was already deleted");
            }
        }

        SletBemyndigelserResponse response = new SletBemyndigelserResponse();
        response.setSlettedeBemyndigelsesKoder(deletedBemyndigelser);
        return response;
    }
}
