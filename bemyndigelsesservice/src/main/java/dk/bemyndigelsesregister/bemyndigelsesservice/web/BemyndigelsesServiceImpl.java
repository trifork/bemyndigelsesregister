package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import com.trifork.dgws.*;
import com.trifork.dgws.annotations.Protected;
import dk.bemyndigelsesregister.bemyndigelsesservice.BemyndigelsesService;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.State;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.BemyndigelseManager;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.DelegationManager;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.bemyndigelse._2012._05._01.*;
import dk.nsi.bemyndigelse._2016._01._01.*;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeader;

import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

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
    DelegationManager delegationManager;
    @Inject
    BemyndigelseDao bemyndigelseDao;
    @Inject
    DelegationDao delegationDao;
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

    @Inject
    WhitelistChecker whitelistChecker;

    public BemyndigelsesServiceImpl() {
    }

    @Override
    @Protected
    @Transactional
    @ResponsePayload
    public OpretAnmodningOmBemyndigelserResponse opretAnmodningOmBemyndigelser(
            @RequestPayload OpretAnmodningOmBemyndigelserRequest request, SoapHeader soapHeader) {

        Collection<Bemyndigelse> createdBemyndigelser = new ArrayList<Bemyndigelse>();

        for (OpretAnmodningOmBemyndigelserRequest.Anmodning anmodning : request.getAnmodning()) {
            authorizeOperationForCpr("opretAnmodningOmBemyndigelser", "IDCard CPR was different from BemyndigedeCpr", anmodning.getBemyndigedeCpr());
            logger.debug("Creating Bemyndigelse for anmodning=" + anmodning.toString());
            final Bemyndigelse bemyndigelse = bemyndigelseManager.opretAnmodningOmBemyndigelse(
                    anmodning.getSystem(),
                    anmodning.getBemyndigendeCpr(),
                    anmodning.getBemyndigedeCpr(),
                    anmodning.getBemyndigedeCvr(),
                    anmodning.getArbejdsfunktion(),
                    anmodning.getRettighed(),
                    anmodning.getSystem(),
                    nullableDateTime(anmodning.getGyldigFra()),
                    nullableDateTime(anmodning.getGyldigTil()));
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
            authorizeOperationForCpr("godkendBemyndigelse", "IDCard CPR var forskelligt fra BemyndigendeCPR på bemyndigelse med koden " + bemyndigelse.getKode(), bemyndigelse.getBemyndigendeCpr());
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
        if (request.getKode() != null) {
            final Bemyndigelse bemyndigelse = bemyndigelseDao.findByKode(request.getKode());
            foundBemyndigelser = singletonList(bemyndigelse);
            authorizeOperationForCpr("hentBemyndigelser", "IDCard CPR was not found in bemyndigelse", bemyndigelse.getBemyndigendeCpr(), bemyndigelse.getBemyndigedeCpr());
        } else if (request.getBemyndigendeCpr() != null) {
            authorizeOperationForCpr("hentBemyndigelser", "IDCard CPR was not equal to BemyndigendeCpr", request.getBemyndigendeCpr());
            foundBemyndigelser = bemyndigelseDao.findByBemyndigendeCpr(request.getBemyndigendeCpr());
        } else if (request.getBemyndigedeCpr() != null) {
            authorizeOperationForCpr("hentBemyndigelser", "IDCard CPR was not equal to BemyndigedeCpr", request.getBemyndigedeCpr());
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

    //TODO KRS check that createDelegation check that state is "BESTILT"
    void authorizeOperationForCpr(String whitelist, String errorMessage, String... authorizedCprs) {
        Set<String> authorizedCprSet = new HashSet<String>(Arrays.asList(authorizedCprs));
        IdCardData idCardData = dgwsRequestContext.getIdCardData();
        if (idCardData.getIdCardType() == IdCardType.SYSTEM) {
            IdCardSystemLog systemLog = dgwsRequestContext.getIdCardSystemLog();
            if (systemLog.getCareProviderIdType() != CareProviderIdType.CVR_NUMBER) {
                throw new IllegalAccessError("Attempted to access operation using system id card, but the CareProviderIdType was not CVR, it was " + systemLog.getCareProviderIdType());
            }
            String cvr = systemLog.getCareProviderId();
            if (!whitelistChecker.isSystemWhitelisted(whitelist, cvr)) {
                throw new IllegalAccessError("Attempted to access operation using system id card, but the whitelist " + whitelist + " did not contain id card CVR " + cvr);
            }
            return;
        } else if (idCardData.getIdCardType() == IdCardType.USER) {
            IdCardUserLog userLog = dgwsRequestContext.getIdCardUserLog();
            if (userLog != null && authorizedCprSet.contains(userLog.cpr)) {
                return;
            } else {
                logger.info("Failed to authorize user id card. Authorized CPRs: " + authorizedCprSet + ". CPR in ID card: " + userLog.cpr);
                throw new IllegalAccessError(errorMessage);
            }
        } else {
            throw new IllegalAccessError("Could not authorize ID card, it was neither a user or system id card");
        }
    }

    public static dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse toJaxbType(final Bemyndigelse bem) {
        return new dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse() {{
            setKode(bem.getKode());
            setBemyndigendeCpr(bem.getBemyndigendeCpr());
            setBemyndigedeCpr(bem.getBemyndigedeCpr());
            setBemyndigedeCvr(bem.getBemyndigedeCvr());
            setSystem(bem.getLinkedSystemKode());
            setArbejdsfunktion(bem.getArbejdsfunktionKode());
            setRettighed(bem.getRettighedKode());
            setStatus(bem.getStatus() == Status.GODKENDT ? "Godkendt" : "Bestilt");
            if (bem.getGodkendelsesdato() != null) {
                setGodkendelsesdato(new XMLGregorianCalendarImpl(bem.getGodkendelsesdato().withZone(DateTimeZone.UTC).toGregorianCalendar()));
            }
            setGyldigFra(new XMLGregorianCalendarImpl(bem.getGyldigFra().withZone(DateTimeZone.UTC).toGregorianCalendar()));
            setGyldigTil(new XMLGregorianCalendarImpl(bem.getGyldigTil().withZone(DateTimeZone.UTC).toGregorianCalendar()));
        }};
    }

    @Override
    @Protected
    @Transactional
    @ResponsePayload
    public OpretGodkendteBemyndigelserResponse opretGodkendtBemyndigelse(
            @RequestPayload final OpretGodkendteBemyndigelserRequest request, SoapHeader soapHeader) {
        Collection<Bemyndigelse> bemyndigelser = new ArrayList<Bemyndigelse>();

        for (final OpretGodkendteBemyndigelserRequest.Bemyndigelse bemyndigelseRequest : request.getBemyndigelse()) {
            authorizeOperationForCpr("opretGodkendtBemyndigelse", "IDCard CPR was different from BemyndigendeCpr", bemyndigelseRequest.getBemyndigendeCpr());
            final Bemyndigelse bemyndigelse = bemyndigelseManager.opretGodkendtBemyndigelse(
                    bemyndigelseRequest.getSystem(),
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
            authorizeOperationForCpr("sletBemyndigelser", "IDCard CPR var forskelligt fra BemyndigendeCPR på bemyndigelse med koden " + bemyndigelse.getKode(), bemyndigelse.getBemyndigendeCpr());

            DateTime validTo = bemyndigelse.getGyldigTil();
            if (validTo.isAfter(now)) {
                logger.info("Deleting bemyndigelse with id=" + bemyndigelse.getKode() + " and kode=" + bemyndigelse.getKode());
                bemyndigelse.setGyldigTil(now);
                bemyndigelse.setSidstModificeret(now);
                bemyndigelseDao.save(bemyndigelse);
                response.getKode().add(bemyndigelse.getKode());
            } else {
                logger.info("Bemyndigelse with id=" + bemyndigelse.getKode() + " and kode=" + bemyndigelse.getKode() + " was already deleted");
            }

        }

        return response;
    }

    private DateTime nullableDateTime(XMLGregorianCalendar xmlDate) {
        return xmlDate != null ? new DateTime(xmlDate.toGregorianCalendar(), DateTimeZone.UTC) : null;
    }

    @Override
    @Transactional
    @ResponsePayload
    @Protected(whitelist = "bemyndigelsesservice.indlaesMetadata")
    public IndlaesMetadataResponse indlaesMetadata(@RequestPayload IndlaesMetadataRequest request, SoapHeader soapHeader) {
        if (request.getArbejdsfunktioner() != null) {
            for (final Arbejdsfunktioner.Arbejdsfunktion jaxbArbejdsfunktion : request.getArbejdsfunktioner().getArbejdsfunktion()) {
                Domaene domaene = domaeneDao.findByKode(jaxbArbejdsfunktion.getDomaene());
                LinkedSystem linkedSystem = linkedSystemDao.findByKode(jaxbArbejdsfunktion.getSystem());
                Arbejdsfunktion arbejdsfunktion = arbejdsfunktionDao.findByKode(linkedSystem, jaxbArbejdsfunktion.getArbejdsfunktion());
                if (arbejdsfunktion != null) {
                    logger.info("Updating arbejdsfunktion=" + jaxbArbejdsfunktion);
                } else {
                    logger.info("Adding arbejdsfunktion=" + jaxbArbejdsfunktion);
                    arbejdsfunktion = new Arbejdsfunktion();
                }
                arbejdsfunktion.setKode(jaxbArbejdsfunktion.getArbejdsfunktion());
                arbejdsfunktion.setBeskrivelse(jaxbArbejdsfunktion.getBeskrivelse());
                arbejdsfunktion.setLinkedSystem(linkedSystem);

                arbejdsfunktionDao.save(arbejdsfunktion);
            }
        }

        if (request.getRettigheder() != null) {
            for (final Rettigheder.Rettighed jaxbRettighed : request.getRettigheder().getRettighed()) {
                LinkedSystem linkedSystem = linkedSystemDao.findByKode(jaxbRettighed.getSystem());

                Rettighed rettighed = rettighedDao.findByKode(linkedSystem, jaxbRettighed.getRettighed());
                if (rettighed != null) {
                    logger.info("Updating rettighed=" + jaxbRettighed);
                } else {
                    logger.info("Adding rettighed=" + jaxbRettighed);
                    rettighed = new Rettighed();
                }
                rettighed.setKode(jaxbRettighed.getRettighed());
                rettighed.setBeskrivelse(jaxbRettighed.getBeskrivelse());
                rettighed.setLinkedSystem(linkedSystem);

                rettighedDao.save(rettighed);
            }
        }

        if (request.getDelegerbarRettigheder() != null) {
            for (final DelegerbarRettigheder.DelegerbarRettighed jaxbDelegerbarRettighed : request.getDelegerbarRettigheder().getDelegerbarRettighed()) {
                LinkedSystem linkedSystem = linkedSystemDao.findByKode(jaxbDelegerbarRettighed.getSystem());
                logger.info("Adding delegerbarRettighed=" + jaxbDelegerbarRettighed.toString());
                DelegerbarRettighed delegerbarRettighed = new DelegerbarRettighed();
                delegerbarRettighed.setArbejdsfunktion(arbejdsfunktionDao.findByKode(linkedSystem, jaxbDelegerbarRettighed.getArbejdsfunktion()));
                delegerbarRettighed.setRettighedskode(rettighedDao.findByKode(linkedSystem, jaxbDelegerbarRettighed.getRettighed()));
                delegerbarRettighedDao.save(delegerbarRettighed);
            }
        }

        return new IndlaesMetadataResponse();
    }

    @Override
    @ResponsePayload
    // TODO Verify with Kjeld that there really is no reason 
    // for hentMetadata to be protected.
    //@Protected(whitelist = "bemyndigelsesservice.hentMetadata")
    public HentMetadataResponse hentMetadata(@RequestPayload HentMetadataRequest request, SoapHeader soapHeader) {
        LinkedSystem linkedSystem = linkedSystemDao.findByKode(request.getSystem());

        final List<Arbejdsfunktion> foundArbejdsfunktioner = arbejdsfunktionDao.findBy(linkedSystem);
        final List<Rettighed> foundRettigheder = rettighedDao.findBy(linkedSystem);
        final List<DelegerbarRettighed> foundDelegerbarRettigheder = delegerbarRettighedDao.findBy(linkedSystem);

        return new HentMetadataResponse() {{
            setArbejdsfunktioner(typeMapper.toJaxbArbejdsfunktioner(foundArbejdsfunktioner));
            setRettigheder(typeMapper.toJaxbRettigheder(foundRettigheder));
            setDelegerbarRettigheder(typeMapper.toJaxbDelegerbarRettigheder(foundDelegerbarRettigheder));
        }};
    }

    @Override
    @Protected
    @Transactional
    @ResponsePayload
    public CreateDelegationsResponse createDelegations(@RequestPayload CreateDelegationsRequest request, SoapHeader soapHeader) {
        Collection<Delegation> delegations = new ArrayList();

        for (CreateDelegationsRequest.Create createDelegation : request.getCreate()) {
            // TODO KRS can also be done by delegatee
            authorizeOperationForCpr("createDelegation", "IDCard CPR was different from DelegatorCpr", createDelegation.getDelegatorCpr());
            logger.debug("Creating Delegation: " + createDelegation.toString());
            final Delegation delegation = delegationManager.createDelegation(
                    createDelegation.getSystemId(),
                    createDelegation.getDelegatorCpr(),
                    createDelegation.getDelegateeCpr(),
                    createDelegation.getDelegateeCvr(),
                    createDelegation.getRoleId(),
                    State.GODKENDT, // valueOf(createDelegation.getState().value()), TODO OBJ Dette fungerede ikke, createDelegation.getState() returnerer null
                    createDelegation.getListOfPermissionIds().getPermissionId(),
                    nullableDateTime(createDelegation.getEffectiveFrom()),
                    nullableDateTime(createDelegation.getEffectiveTo()));
            logger.debug("Got delegation with domain id = " + delegation.getDomainId());

            delegations.add(delegation);
        }

        final CreateDelegationsResponse response = new CreateDelegationsResponse();
        for (Delegation delegation : delegations) {
            response.getDelegation().add(typeMapper.toDelegationType(delegation));
        }
        return response;
    }

    @Override
    @Protected
    @Transactional
    @ResponsePayload
    public GetDelegationsResponse getDelegations(@RequestPayload GetDelegationsRequest request, SoapHeader soapHeader) {
        Collection<Delegation> delegations = new ArrayList();

        String delegatorCpr = request.getDelegatorCpr();
        String delegateeCpr = request.getDelegateeCpr();
        String delegationId = request.getDelegationId();

        // check arguments
        if ((delegatorCpr != null ? 1 : 0) + (delegateeCpr != null ? 1 : 0) + (delegationId != null ? 1 : 0) != 1) {
            throw new IllegalArgumentException("A single argument must be supplied, i.e. exactly one of delegatorCpr, delegateeCpr or delegationId must not be null");
        }

        // invoke correct method on manager
        if (delegatorCpr != null) {
            List<Delegation> list = delegationManager.getDelegationsByDelegatorCpr(delegatorCpr);
            if (list != null) {
                delegations.addAll(list);
            }
        } else if (delegateeCpr != null) {
            List<Delegation> list = delegationManager.getDelegationsByDelegateeCpr(delegateeCpr);
            if (list != null) {
                delegations.addAll(list);
            }
        } else {
            Delegation d = delegationManager.getDelegation(delegationId);
            if (d != null) {
                delegations.add(d);
            }
        }

        // return result
        final GetDelegationsResponse response = new GetDelegationsResponse();
        for (Delegation delegation : delegations) {
            response.getDelegation().add(typeMapper.toDelegationType(delegation));
        }
        return response;
    }

    @Override
    @Protected
    @Transactional
    @ResponsePayload
    public DeleteDelegationsResponse deleteDelegations(@RequestPayload DeleteDelegationsRequest request, SoapHeader soapHeader) {
        List<String> delegationIds = request.getDelegationId();
        XMLGregorianCalendar xmlDate = request.getDeletionDate();
        DateTime deletionDate = xmlDate == null ? null : new DateTime(xmlDate.toGregorianCalendar().getTimeInMillis());

        // check arguments
        if (delegationIds == null || delegationIds.isEmpty())
            throw new IllegalArgumentException("List of delegationIds must not be empty");
        if (deletionDate == null)
            throw new IllegalArgumentException("DeletionDate must be specified");
        if (deletionDate.isAfterNow())
            throw new IllegalArgumentException("DeletionDate cannot be a future date");

        // invoke manager
        List<String> result = new LinkedList<>();
        for (String delegationId : delegationIds) {
            result.add(delegationManager.deleteDelegation(delegationId, deletionDate));
        }

        // return result
        final DeleteDelegationsResponse response = new DeleteDelegationsResponse();
        response.getDelegationId().addAll(result);
        return response;
    }
}
