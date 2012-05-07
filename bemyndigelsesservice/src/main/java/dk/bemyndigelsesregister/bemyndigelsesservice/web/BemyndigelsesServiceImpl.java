package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import com.trifork.dgws.annotations.Protected;
import dk.bemyndigelsesregister.bemyndigelsesservice.BemyndigelsesService;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import com.trifork.dgws.util.SecurityHelper;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.bemyndigelse._2012._05._01.*;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
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
import java.util.Collection;
import java.util.Collections;

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
        bemyndigelse.setLinkedSystem(linkedSystemDao.findBySystem(request.getSystem()));
        final DateTime now = systemService.getDateTime();
        bemyndigelse.setGyldigFra(now);
        bemyndigelse.setGyldigTil(now.plusYears(99));
        bemyndigelse.setVersionsid(1);

        bemyndigelseDao.save(bemyndigelse);
        return new OpretAnmodningOmBemyndigelseResponse();
    }

    @Override
    @Protected(whitelist = "BemyndigelsesService.godkendBemyndigelse")
    @Transactional
    public @ResponsePayload GodkendBemyndigelseResponse godkendBemyndigelse(
            @RequestPayload GodkendBemyndigelseRequest request, SoapHeader soapHeader) {
        final Bemyndigelse bemyndigelse = bemyndigelseDao.findByKode(request.getBemyndigelsesKode());

        bemyndigelse.setGodkendelsesdato(systemService.getDateTime());

        bemyndigelseDao.save(bemyndigelse);

        final GodkendBemyndigelseResponse response = new GodkendBemyndigelseResponse();
        response.setGodkendtBemyndigelsesKode(bemyndigelse.getKode());
        return response;
    }

    @Override
    @Protected(whitelist = "BemyndigelsesService.hentBemyndigelser")
    public @ResponsePayload HentBemyndigelserResponse hentBemyndigelser(
            @RequestPayload HentBemyndigelserRequest request, SoapHeader soapHeader) {
        Collection<Bemyndigelse> foundBemyndigelser = Collections.emptyList();
        if (request.getBemyndigende() != null) {
            foundBemyndigelser = bemyndigelseDao.findByBemyndigendeCpr(request.getBemyndigende());
        }
        else if (request.getBemyndigede() != null) {
            foundBemyndigelser = bemyndigelseDao.findByBemyndigedeCpr(request.getBemyndigede());
        }

        final Collection<Bemyndigelse> finalFoundBemyndigelser = foundBemyndigelser;

        return new HentBemyndigelserResponse() {{
            getBemyndigelser().addAll(CollectionUtils.collect(
                    finalFoundBemyndigelser,
                    new Transformer<Bemyndigelse, dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse>() {
                        @Override
                        public dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse transform(final Bemyndigelse bem) {
                            return new dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse() {{
                                setKode(bem.getKode());
                                setBemyndigende(bem.getBemyndigendeCpr());
                                setBemyndigede(bem.getBemyndigedeCpr());
                                setBemyndigedeCvr(bem.getBemyndigedeCvr());
                                setSystem(bem.getLinkedSystem().getSystem());
                                setArbejdsfunktion(bem.getArbejdsfunktion().getArbejdsfunktion());
                                setRettighedskode(bem.getRettighed().getRettighedskode());
                                setStatus(bem.getStatus().getStatus());
                                setGodkendelsesdato(new XMLGregorianCalendarImpl(bem.getGodkendelsesdato().toGregorianCalendar()));
                                setGyldigFra(new XMLGregorianCalendarImpl(bem.getGyldigFra().toGregorianCalendar()));
                                setGyldigTil(new XMLGregorianCalendarImpl(bem.getGyldigTil().toGregorianCalendar()));
                            }};
                        }
                    }
            ));
        }};
    }

    @Override
    @Protected(whitelist = "BemyndigelsesService.opretGodkendtBemyndigelse")
    @Transactional
    public @ResponsePayload OpretGodkendtBemyndigelseResponse opretGodkendtBemyndigelse(
            @RequestPayload final OpretGodkendtBemyndigelseRequest request) {
        Bemyndigelse bemyndigelse = new Bemyndigelse() {{
            setBemyndigendeCpr(request.getBemyndigende());
            setBemyndigedeCpr(request.getBemyndigede());
            setBemyndigedeCvr(request.getBemyndigedeCVR());
            setLinkedSystem(linkedSystemDao.findBySystem(request.getSystem()));
            setArbejdsfunktion(arbejdsfunktionDao.findByArbejdsfunktion(request.getArbejdsfunktion()));
            setRettighed(rettighedDao.findByRettighedskode(request.getRettighedskode()));

            setKode(systemService.createUUIDString());
            setGodkendelsesdato(systemService.getDateTime());
        }};

        final OpretGodkendtBemyndigelseResponse response = new OpretGodkendtBemyndigelseResponse();
        response.setGodkendtBemyndigelsesKode(bemyndigelse.getKode());
        bemyndigelseDao.save(bemyndigelse);
        return response;
    }

    @Override
    @Protected(whitelist = "BemyndigelsesService.sletBemyndigelser")
    @Transactional
    public @ResponsePayload SletBemyndigelserResponse sletBemyndigelser(
            @RequestPayload SletBemyndigelserRequest request, SoapHeader soapHeader) {

        String cpr = securityHelper.getCpr(soapHeader);

        DateTime now = systemService.getDateTime();

        SletBemyndigelserResponse response = new SletBemyndigelserResponse();

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
                response.getSlettedeBemyndigelsesKoder().add(bemyndigelse.getKode());
            }
            else {
                logger.info("Bemyndigelse with id=" + bemyndigelse.getId() + " and kode=" + bemyndigelse.getKode() + " was already deleted");
            }
        }

        return response;
    }
}
