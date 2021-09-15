package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.WhitelistType;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.DelegationManager;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.MetadataManager;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.audit.AuditLogger;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.WhitelistDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.SystemService;
import dk.sds.nsp.security.SecurityContext;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

public abstract class AbstractServiceImpl {
    @Inject
    SystemService systemService;
    @Inject
    DelegationManager delegationManager;
    @Inject
    MetadataManager metadataManager;
    @Inject
    AuditLogger auditLogger;
    @Inject
    WhitelistDao whitelistDao;

    private Logger logger;

    public AbstractServiceImpl(Logger logger) {
        this.logger = logger;
    }

    protected void checkSecurityTicket(SecurityContext securityContext) {
        checkSecurityTicket(securityContext, true, null);
    }

    protected void checkSecurityTicket(SecurityContext securityContext, boolean userRequired, String whitelist) {
        Optional<SecurityContext.Ticket> ticketOptional;
        try {
            ticketOptional = securityContext.getTicket();
        } catch (Exception ex) { // avoid NPE in NspSecurityContext.java:113
            throw new SecurityException("No security ticket is present");
        }
        if (!ticketOptional.isPresent()) {
            throw new SecurityException("No security ticket is present");
        }
        SecurityContext.Ticket ticket = ticketOptional.get();
        if (!ticket.isValid()) {
            throw new SecurityException("Invalid security ticket");
        }
        if (userRequired && !securityContext.getActingUser().isPresent()) {
            throw new SecurityException("Calling user not found in security context");
        }
        if (whitelist != null) {
            if (!securityContext.getOrganisation().isPresent()) {
                throw new SecurityException("Calling organisation not found in security context");
            }

            SecurityContext.Organisation organisation = securityContext.getOrganisation().get();
            if (organisation.getIdentifierFormat() != SecurityContext.Organisation.OrganisationIdentifierFormat.CVR) {
                throw new SecurityException("Unsupported organisation identifier format " + organisation.getIdentifierFormat() + ". Only CVR is supported");
            }

            if (!whitelistDao.exists(whitelist, WhitelistType.SYSTEM_CVR, organisation.getIdentifier())) {
                throw new SecurityException("Organisation " + organisation.getIdentifier() + " not whitelisted for " + whitelist);
            }
        }
    }

    protected void authorizeOperationForCpr(SecurityContext securityContext, String errorMessage, String... authorizedCprs) {
        Set<String> authorizedCprSet = new HashSet<>(Arrays.asList(authorizedCprs));

        if (!securityContext.getActingUser().isPresent()) {
            throw new SecurityException("Calling user not found in security context");
        }

        String cprnr = securityContext.getActingUser().get().getIdentifier();
        if (!authorizedCprSet.contains(cprnr)) {
            logger.info("Failed to authorize user. Authorized CPRs: " + authorizedCprSet + ". CPR in securityContext: [" + cprnr + "]");
            throw new SecurityException(errorMessage);
        }
    }

    protected DateTime nullableDateTime(XMLGregorianCalendar xmlDate) {
        return xmlDate != null ? new DateTime(xmlDate.toGregorianCalendar(), DateTimeZone.UTC) : null;
    }

    protected List<Delegation> getDelegationsCommon(String delegatorCpr, String delegateeCpr, String delegationId, XMLGregorianCalendar effectiveFrom, XMLGregorianCalendar effectiveTo, SecurityContext securityContext) {
        auditLogger.log("Hent bemyndigelser", delegateeCpr, securityContext);

        List<Delegation> delegations = new LinkedList<>();

        // check arguments
        if ((delegatorCpr != null ? 1 : 0) + (delegateeCpr != null ? 1 : 0) + (delegationId != null ? 1 : 0) != 1) {
            throw new IllegalArgumentException("A single argument must be supplied, i.e. exactly one of delegatorCpr, delegateeCpr or delegationId must not be null");
        }

        // authorize
        if (delegatorCpr != null)
            authorizeOperationForCpr(securityContext, "CPR for calling user was different from DelegatorCpr", delegatorCpr);
        else if (delegateeCpr != null)
            authorizeOperationForCpr(securityContext, "CPR for calling user was different from DelegateeCpr", delegateeCpr);

        // invoke correct method on manager
        if (delegatorCpr != null) {
            List<Delegation> list = delegationManager.getDelegationsByDelegatorCpr(delegatorCpr, nullableDateTime(effectiveFrom), nullableDateTime(effectiveTo));
            if (list != null) {
                delegations.addAll(list);
            }
        } else if (delegateeCpr != null) {
            List<Delegation> list = delegationManager.getDelegationsByDelegateeCpr(delegateeCpr, nullableDateTime(effectiveFrom), nullableDateTime(effectiveTo));
            if (list != null) {
                delegations.addAll(list);
            }
        } else {
            Delegation d = delegationManager.getDelegation(delegationId);
            if (d != null) {
                delegations.add(d);
            }
        }

        List<Delegation> result = new LinkedList<>();
        for (Delegation delegation : delegations) {
            if (delegation.getEffectiveTo() == null || delegation.getEffectiveFrom().isBefore(delegation.getEffectiveTo())) {
                result.add(delegation);
            }
        }

        return result;
    }

    protected List<String> deleteDelegationsCommon(String delegatorCpr, String delegateeCpr, List<String> delegationIds, XMLGregorianCalendar xmlDate, SecurityContext securityContext) {
        auditLogger.log("Slet bemyndigelser", delegateeCpr, securityContext);

        DateTime deletionDate = xmlDate == null ? null : new DateTime(xmlDate.toGregorianCalendar().getTimeInMillis());

        // check arguments
        if (delegatorCpr == null) {
            if (delegateeCpr == null) {
                throw new IllegalArgumentException("Exactly one of delegatorCpr and delegateeCpr must be supplied");
            }
        } else {
            if (delegateeCpr != null) {
                throw new IllegalArgumentException("Exactly one of delegatorCpr and delegateeCpr must be supplied");
            }
        }
        if (delegationIds == null || delegationIds.isEmpty())
            throw new IllegalArgumentException("List of delegationIds must not be empty");
        if (deletionDate != null && deletionDate.isBeforeNow())
            throw new IllegalArgumentException("DeletionDate cannot be in the past");

        // authorize
        if (delegatorCpr != null)
            authorizeOperationForCpr(securityContext, "CPR for calling user was different from DelegatorCpr", delegatorCpr);
        else
            authorizeOperationForCpr(securityContext, "CPR for calling user was different from DelegateeCpr", delegateeCpr);

        // invoke manager
        List<String> result = new LinkedList<>();
        for (String delegationId : delegationIds) {
            String deleted = delegationManager.deleteDelegation(delegatorCpr, delegateeCpr, delegationId, deletionDate);
            if (deleted != null) result.add(deleted);
        }
        return result;
    }
}
