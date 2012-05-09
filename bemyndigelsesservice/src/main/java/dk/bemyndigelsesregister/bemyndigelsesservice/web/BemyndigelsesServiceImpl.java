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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeader;

import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

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
    SecurityHelper securityHelper;

    public BemyndigelsesServiceImpl() {
    }

    @Override
    @Protected(whitelist = "BemyndigelsesService.opretAnmodningOmBemyndigelser")
    @Transactional
    public @ResponsePayload OpretAnmodningOmBemyndigelserResponse opretAnmodningOmBemyndigelser(
            @RequestPayload OpretAnmodningOmBemyndigelserRequest request, SoapHeader soapHeader) {
        Collection<Bemyndigelse> createdBemyndigelser = new ArrayList<Bemyndigelse>();

        for (OpretAnmodningOmBemyndigelserRequest.Anmodning anmodning : request.getAnmodning()) {
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

        final OpretAnmodningOmBemyndigelserResponse response = new OpretAnmodningOmBemyndigelserResponse();
        for (Bemyndigelse bemyndigelse : createdBemyndigelser) {
            response.getBemyndigelse().add(toJaxbType(bemyndigelse));
        }
        return response;
    }

    @Override
    @Protected(whitelist = "BemyndigelsesService.godkendBemyndigelse")
    @Transactional
    public @ResponsePayload GodkendBemyndigelseResponse godkendBemyndigelse(
            @RequestPayload GodkendBemyndigelseRequest request, SoapHeader soapHeader) {
        final List<String> bemyndigelsesKoder = request.getBemyndigelsesKode();

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
        if (request.getKode() != null) {
            foundBemyndigelser = singletonList(bemyndigelseDao.findByKode(request.getKode()));
        }
        else if (request.getBemyndigendeCpr() != null) {
            foundBemyndigelser = bemyndigelseDao.findByBemyndigendeCpr(request.getBemyndigendeCpr());
        }
        else if (request.getBemyndigedeCpr() != null) {
            foundBemyndigelser = bemyndigelseDao.findByBemyndigedeCpr(request.getBemyndigedeCpr());
        }

        final Collection<Bemyndigelse> finalFoundBemyndigelser = foundBemyndigelser;

        return new HentBemyndigelserResponse() {{
            getBemyndigelse().addAll(CollectionUtils.collect(
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
            setBemyndigendeCpr(bem.getBemyndigendeCpr());
            setBemyndigedeCpr(bem.getBemyndigedeCpr());
            setBemyndigedeCvr(bem.getBemyndigedeCvr());
            setSystem(bem.getLinkedSystem().getSystem());
            setArbejdsfunktion(bem.getArbejdsfunktion().getArbejdsfunktion());
            setRettighed(bem.getRettighed().getRettighedskode());
            setStatus(bem.getStatus().getStatus());
            if (bem.getGodkendelsesdato() != null) {
                setGodkendelsesdato(new XMLGregorianCalendarImpl(bem.getGodkendelsesdato().toGregorianCalendar()));
            }
            setGyldigFra(new XMLGregorianCalendarImpl(bem.getGyldigFra().toGregorianCalendar()));
            setGyldigTil(new XMLGregorianCalendarImpl(bem.getGyldigTil().toGregorianCalendar()));
        }};
    }

    @Override
    @Protected(whitelist = "BemyndigelsesService.opretGodkendteBemyndigelser")
    @Transactional
    public @ResponsePayload OpretGodkendteBemyndigelserResponse opretGodkendtBemyndigelse(
            @RequestPayload final OpretGodkendteBemyndigelserRequest request, SoapHeader soapHeader) {
        Collection<Bemyndigelse> bemyndigelser = new ArrayList<Bemyndigelse>();

        for (final OpretGodkendteBemyndigelserRequest.Bemyndigelse bemyndigelseRequest : request.getBemyndigelse()) {
            final Bemyndigelse bemyndigelse = bemyndigelseManager.opretGodkendtBemyndigelse(
                    bemyndigelseRequest.getBemyndigendeCpr(),
                    bemyndigelseRequest.getBemyndigedeCpr(),
                    bemyndigelseRequest.getBemyndigedeCvr(),
                    bemyndigelseRequest.getArbejdsfunktion(),
                    bemyndigelseRequest.getRettighed(),
                    bemyndigelseRequest.getSystem(),
                    nullableDateTime(bemyndigelseRequest.getGyldigFra()),
                    nullableDateTime(bemyndigelseRequest.getGyldigTil())
            );
            bemyndigelser.add(bemyndigelse);
        }

        final OpretGodkendteBemyndigelserResponse response = new OpretGodkendteBemyndigelserResponse();
        response.getBemyndigelse().addAll(CollectionUtils.collect(
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

        for (String kode : request.getKode()) {
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
                response.getKode().add(bemyndigelse.getKode());
            }
            else {
                logger.info("Bemyndigelse with id=" + bemyndigelse.getId() + " and kode=" + bemyndigelse.getKode() + " was already deleted");
            }
        }

        return response;
    }

    private DateTime nullableDateTime(XMLGregorianCalendar xmlDate) {
        return xmlDate != null ? new DateTime(xmlDate.toGregorianCalendar()) : null;
    }
}
