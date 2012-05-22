package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import com.trifork.dgws.DgwsRequestContext;
import com.trifork.dgws.annotations.Protected;
import dk.bemyndigelsesregister.bemyndigelsesservice.BemyndigelsesService;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.BemyndigelseManager;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.bemyndigelse._2012._05._01.*;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.StringUtils;
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
    DomaeneDao domaeneDao;
    @Inject
    LinkedSystemDao linkedSystemDao;
    @Inject
    ArbejdsfunktionDao arbejdsfunktionDao;
    @Inject
    RettighedDao rettighedDao;
    @Inject
    DelegerbarRettighedDao delegerbarRettighedDao;
    @Inject
    DgwsRequestContext dgwsRequestContext;
    @Inject
    ServiceTypeMapper typeMapper;

    public BemyndigelsesServiceImpl() {
    }

    @Override
    @Protected
    @Transactional
    @ResponsePayload
    public OpretAnmodningOmBemyndigelserResponse opretAnmodningOmBemyndigelser(
            @RequestPayload OpretAnmodningOmBemyndigelserRequest request, SoapHeader soapHeader) {
        String idCardCpr = dgwsRequestContext.getIdCardCpr();

        Collection<Bemyndigelse> createdBemyndigelser = new ArrayList<Bemyndigelse>();

        for (OpretAnmodningOmBemyndigelserRequest.Anmodning anmodning : request.getAnmodning()) {
            verifyCprIn(idCardCpr, "IDCard CPR was different from BemyndigedeCpr", anmodning.getBemyndigedeCpr());
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
    @Protected
    @Transactional
    @ResponsePayload
    public GodkendBemyndigelseResponse godkendBemyndigelse(
            @RequestPayload GodkendBemyndigelseRequest request, SoapHeader soapHeader) {
        final List<String> bemyndigelsesKoder = request.getBemyndigelsesKode();

        Collection<Bemyndigelse> bemyndigelser = bemyndigelseManager.godkendBemyndigelser(bemyndigelsesKoder);

        for (Bemyndigelse bemyndigelse : bemyndigelser) {
            verifyCprIn(dgwsRequestContext.getIdCardCpr(), "IDCard CPR var forskelligt fra BemyndigendeCPR på bemyndigelse med koden " + bemyndigelse.getKode(), bemyndigelse.getBemyndigendeCpr());
        }

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
    @Protected
    @ResponsePayload
    public HentBemyndigelserResponse hentBemyndigelser(
            @RequestPayload HentBemyndigelserRequest request, SoapHeader soapHeader) {
        List<Bemyndigelse> foundBemyndigelser = Collections.emptyList();
        String idCardCpr = dgwsRequestContext.getIdCardCpr();
        if (request.getKode() != null) {
            final Bemyndigelse bemyndigelse = bemyndigelseDao.findByKode(request.getKode());
            foundBemyndigelser = singletonList(bemyndigelse);
            verifyCprIn(idCardCpr, "IDCard CPR was not found in bemyndigelse", bemyndigelse.getBemyndigendeCpr(), bemyndigelse.getBemyndigedeCpr());
        }
        else if (request.getBemyndigendeCpr() != null) {
            verifyCprIn(idCardCpr, "IDCard CPR was not equal to BemyndigendeCpr", request.getBemyndigendeCpr());
            foundBemyndigelser = bemyndigelseDao.findByBemyndigendeCpr(request.getBemyndigendeCpr());
        }
        else if (request.getBemyndigedeCpr() != null) {
            verifyCprIn(idCardCpr, "IDCard CPR was not equal to BemyndigedeCpr", request.getBemyndigedeCpr());
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

    public void verifyCprIn(String idCardCpr, String errorMessage, String... cprs) {
        for (String cpr : cprs) {
            if (idCardCpr.equals(cpr)) {
                return;
            }
        }
        logger.error("IDCard cpr=" + idCardCpr + " was not found in[" + StringUtils.join(cprs, ",") + "]");
        throw new IllegalAccessError(errorMessage);
    }

    public static dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse toJaxbType(final Bemyndigelse bem) {
        return new dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse() {{
            setKode(bem.getKode());
            setBemyndigendeCpr(bem.getBemyndigendeCpr());
            setBemyndigedeCpr(bem.getBemyndigedeCpr());
            setBemyndigedeCvr(bem.getBemyndigedeCvr());
            setSystem(bem.getLinkedSystem().getKode());
            setArbejdsfunktion(bem.getArbejdsfunktion().getKode());
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
    @Protected
    @Transactional
    @ResponsePayload
    public OpretGodkendteBemyndigelserResponse opretGodkendtBemyndigelse(
            @RequestPayload final OpretGodkendteBemyndigelserRequest request, SoapHeader soapHeader) {
        Collection<Bemyndigelse> bemyndigelser = new ArrayList<Bemyndigelse>();

        final String idCardCpr = dgwsRequestContext.getIdCardCpr();

        for (final OpretGodkendteBemyndigelserRequest.Bemyndigelse bemyndigelseRequest : request.getBemyndigelse()) {
            verifyCprIn(idCardCpr, "IDCard CPR was different from BemyndigendeCpr", bemyndigelseRequest.getBemyndigendeCpr());
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
    @Protected
    @Transactional
    @ResponsePayload
    public SletBemyndigelserResponse sletBemyndigelser(
            @RequestPayload SletBemyndigelserRequest request, SoapHeader soapHeader) {

        DateTime now = systemService.getDateTime();

        SletBemyndigelserResponse response = new SletBemyndigelserResponse();

        List<Bemyndigelse> bemyndigelser = bemyndigelseDao.findByKoder(request.getKode());
        for (Bemyndigelse bemyndigelse : bemyndigelser) {
            verifyCprIn(dgwsRequestContext.getIdCardCpr(), "IDCard CPR var forskelligt fra BemyndigendeCPR på bemyndigelse med koden " + bemyndigelse.getKode(), bemyndigelse.getBemyndigendeCpr());

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

    @Override
    @Transactional
    public IndlaesMetadataResponse indlaesMetadata(IndlaesMetadataRequest request, SoapHeader soapHeader) {
        if (request.getArbejdsfunktioner() != null) {
            for (final Arbejdsfunktioner.Arbejdsfunktion jaxbArbejdsfunktion : request.getArbejdsfunktioner().getArbejdsfunktion()) {
                logger.info("Adding arbejdsfunktion=" + jaxbArbejdsfunktion);
                arbejdsfunktionDao.save(new Arbejdsfunktion() {{
                    this.setKode(jaxbArbejdsfunktion.getArbejdsfunktion());
                    this.setBeskrivelse(jaxbArbejdsfunktion.getBeskrivelse());
                    this.setDomaene(domaeneDao.findByKode(jaxbArbejdsfunktion.getDomaene()));
                    this.setLinkedSystem(linkedSystemDao.findByKode(jaxbArbejdsfunktion.getSystem()));
                }});
            }
        }

        if (request.getRettigheder() != null) {
            for (final Rettigheder.Rettighed jaxbRettighed : request.getRettigheder().getRettighed()) {
                logger.info("Adding rettighed=" + jaxbRettighed.toString());
                rettighedDao.save(new Rettighed() {{
                    this.setRettighedskode(jaxbRettighed.getRettighed());
                    this.setBeskrivelse(jaxbRettighed.getBeskrivelse());
                    this.setDomaene(domaeneDao.findByKode(jaxbRettighed.getDomaene()));
                    this.setLinkedSystem(linkedSystemDao.findByKode(jaxbRettighed.getSystem()));
                }});
            }
        }

        if (request.getDelegerbarRettigheder() != null) {
            for (final DelegerbarRettigheder.DelegerbarRettighed jaxbDelegerbarRettighed : request.getDelegerbarRettigheder().getDelegerbarRettighed()) {
                logger.info("Adding delegerbarRettighed=" + jaxbDelegerbarRettighed.toString());
                delegerbarRettighedDao.save(new DelegerbarRettighed() {{
                    this.setArbejdsfunktion(arbejdsfunktionDao.findByKode(jaxbDelegerbarRettighed.getArbejdsfunktion()));
                    this.setKode(jaxbDelegerbarRettighed.getRettighed());
                    this.setLinkedSystem(linkedSystemDao.findByKode(jaxbDelegerbarRettighed.getSystem()));
                    this.setDomaene(domaeneDao.findByKode(jaxbDelegerbarRettighed.getDomaene()));
                }});
            }
        }

        return new IndlaesMetadataResponse();
    }

    @Override
    public HentMetadataResponse hentMetadata(HentMetadataRequest request, SoapHeader soapHeader) {
        Domaene domaene = domaeneDao.findByKode(request.getDomaene());
        LinkedSystem linkedSystem = linkedSystemDao.findByKode(request.getSystem());

        final List<Arbejdsfunktion> foundArbejdsfunktioner = arbejdsfunktionDao.findBy(domaene, linkedSystem);
        final List<Rettighed> foundRettigheder = rettighedDao.findBy(domaene, linkedSystem);
        final List<DelegerbarRettighed> foundDelegerbarRettigheder = delegerbarRettighedDao.findBy(domaene, linkedSystem);

        return new HentMetadataResponse() {{
            setArbejdsfunktioner(typeMapper.toJaxbArbejdsfunktioner(foundArbejdsfunktioner));
            setRettigheder(typeMapper.toJaxbRettigheder(foundRettigheder));
            setDelegerbarRettigheder(typeMapper.toJaxbDelegerbarRettigheder(foundDelegerbarRettigheder));
        }};
    }
}
