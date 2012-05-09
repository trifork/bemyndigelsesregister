package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import com.trifork.dgws.annotations.Protected;
import dk.bemyndigelsesregister.bemyndigelsesservice.BemyndigelsesService;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import com.trifork.dgws.util.SecurityHelper;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.BemyndigelseManager;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository("bemyndigelsesService")
@Endpoint
public class BemyndigelsesServiceImpl implements BemyndigelsesService {
    private static Logger logger = Logger.getLogger(BemyndigelsesServiceImpl.class);
    @Inject
    SystemService systemService;
    @Inject
    BemyndigelseManager bemyndigelseManager;
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
        Collection<Bemyndigelse> createdBemyndigelser = new ArrayList<Bemyndigelse>();

        for (OpretAnmodningOmBemyndigelseRequest.Anmodninger anmodning : request.getAnmodninger()) {
            logger.debug("Creating Bemyndigelse for anmodning=" + anmodning.toString());
            final Bemyndigelse bemyndigelse = bemyndigelseManager.opretAnmodningOmBemyndigelse(
                    anmodning.getBemyndigendeCpr(),
                    anmodning.getBemyndigedeCpr(),
                    anmodning.getBemyndigedeCvr(),
                    anmodning.getArbejdsfunktion(),
                    anmodning.getRettighed(),
                    anmodning.getSystem(),
                    null, null);
            logger.debug("Got bemyndigelse with kode=" + bemyndigelse.getKode());
            createdBemyndigelser.add(bemyndigelse);
        }

        final OpretAnmodningOmBemyndigelseResponse response = new OpretAnmodningOmBemyndigelseResponse();
        for (Bemyndigelse bemyndigelse : createdBemyndigelser) {
            response.getBemyndigelser().add(toJaxbType(bemyndigelse));
        }
        return response;
    }

    @Override
    @Protected(whitelist = "BemyndigelsesService.godkendBemyndigelse")
    @Transactional
    public @ResponsePayload GodkendBemyndigelseResponse godkendBemyndigelse(
            @RequestPayload GodkendBemyndigelseRequest request, SoapHeader soapHeader) {
        final List<String> bemyndigelsesKoder = request.getBemyndigelsesKoder();

        Collection<Bemyndigelse> bemyndigelser = bemyndigelseManager.godkendBemyndigelser(bemyndigelsesKoder);

        final GodkendBemyndigelseResponse response = new GodkendBemyndigelseResponse();
        response.getBemyndigelser().addAll(CollectionUtils.collect(
                bemyndigelser,
                new Transformer<Bemyndigelse, dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse>() {
                    @Override
                    public dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse transform(Bemyndigelse bemyndigelse) {
                        return toJaxbType(bemyndigelse);
                    }
                }
        ));
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
                            return toJaxbType(bem);
                        }
                    }
            ));
        }};
    }

    public static dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse toJaxbType(final Bemyndigelse bem) {
        return new dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse() {{
            setKode(bem.getKode());
            setBemyndigende(bem.getBemyndigendeCpr());
            setBemyndigede(bem.getBemyndigedeCpr());
            setBemyndigedeCvr(bem.getBemyndigedeCvr());
            setSystem(bem.getLinkedSystem().getSystem());
            setArbejdsfunktion(bem.getArbejdsfunktion().getArbejdsfunktion());
            setRettighedskode(bem.getRettighed().getRettighedskode());
            setStatus(bem.getStatus().getStatus());
            if (bem.getGodkendelsesdato() != null) {
                setGodkendelsesdato(new XMLGregorianCalendarImpl(bem.getGodkendelsesdato().toGregorianCalendar()));
            }
            setGyldigFra(new XMLGregorianCalendarImpl(bem.getGyldigFra().toGregorianCalendar()));
            setGyldigTil(new XMLGregorianCalendarImpl(bem.getGyldigTil().toGregorianCalendar()));
        }};
    }

    @Override
    @Protected(whitelist = "BemyndigelsesService.opretGodkendtBemyndigelse")
    @Transactional
    public @ResponsePayload OpretGodkendtBemyndigelseResponse opretGodkendtBemyndigelse(
            @RequestPayload final OpretGodkendtBemyndigelseRequest request, SoapHeader soapHeader) {
        Collection<Bemyndigelse> bemyndigelser = new ArrayList<Bemyndigelse>();
        final DateTime now = systemService.getDateTime();

        for (final OpretGodkendtBemyndigelseRequest.Bemyndigelser bemyndigelseRequest : request.getBemyndigelser()) {
            final Bemyndigelse bemyndigelse = new Bemyndigelse() {{
                setBemyndigendeCpr(bemyndigelseRequest.getBemyndigende());
                setBemyndigedeCpr(bemyndigelseRequest.getBemyndigede());
                setBemyndigedeCvr(bemyndigelseRequest.getBemyndigedeCVR());
                setLinkedSystem(linkedSystemDao.findBySystem(bemyndigelseRequest.getSystem()));
                setArbejdsfunktion(arbejdsfunktionDao.findByArbejdsfunktion(bemyndigelseRequest.getArbejdsfunktion()));
                setRettighed(rettighedDao.findByRettighedskode(bemyndigelseRequest.getRettighed()));
                setKode(systemService.createUUIDString());
                setStatus(statusTypeDao.get(0));
                setGodkendelsesdato(now);
                setGyldigFra(now);
                setGyldigTil(now.plusYears(100));

            }};
            bemyndigelseDao.save(bemyndigelse);
            bemyndigelser.add(bemyndigelse);
        }

        final OpretGodkendtBemyndigelseResponse response = new OpretGodkendtBemyndigelseResponse();
        response.getBemyndigelser().addAll(CollectionUtils.collect(
                bemyndigelser,
                new Transformer<Bemyndigelse, dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse>() {
                    @Override
                    public dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse transform(Bemyndigelse bemyndigelse) {
                        return toJaxbType(bemyndigelse);
                    }
                }
        ));
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
