package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.trifork.dgws.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.DelegationManager;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.MetadataManager;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.audit.AuditLogger;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

/**
 * Created by obj on 17-08-2017.
 */
public abstract class AbstractServiceImpl {
    @Inject
    SystemService systemService;
    @Inject
    DelegationManager delegationManager;
    @Inject
    MetadataManager metadataManager;
    @Inject
    DgwsRequestContext dgwsRequestContext;
    @Inject
    WhitelistChecker whitelistChecker;
    @Inject
    AuditLogger auditLogger;

    private Logger logger;

    public AbstractServiceImpl(Logger logger) {
        this.logger = logger;
    }

    protected void authorizeOperationForCpr(String whitelist, String errorMessage, String... authorizedCprs) {
        Set<String> authorizedCprSet = new HashSet<>(Arrays.asList(authorizedCprs));
        IdCardData idCardData = dgwsRequestContext.getIdCardData();
        if (idCardData.getIdCardType() == IdCardType.SYSTEM) {
            IdCardSystemLog systemLog = dgwsRequestContext.getIdCardSystemLog();
            if (systemLog.getCareProviderIdType() != CareProviderIdType.CVR_NUMBER) {
                throw new IllegalAccessError("Attempted to access operation using system id card, but the CareProviderIdType was not CVR, it was " + systemLog.getCareProviderIdType());
            }
            String cvr = systemLog.getCareProviderId();
            if (!whitelistChecker.isSystemWhitelisted(whitelist, cvr)) {
                throw new IllegalAccessError("Attempted to access operation using system id card, but the whitelist " + whitelist + " did not contain id card CVR [" + cvr + "]");
            }
        } else if (idCardData.getIdCardType() == IdCardType.USER) {
            IdCardUserLog userLog = dgwsRequestContext.getIdCardUserLog();
            if (userLog == null || !authorizedCprSet.contains(userLog.cpr)) {
                logger.info("Failed to authorize user id card. Authorized CPRs: " + authorizedCprSet + ". CPR in ID card: [" + (userLog != null ? userLog.cpr : null) + "]");
                throw new IllegalAccessError(errorMessage);
            }
        } else {
            throw new IllegalAccessError("Could not authorize ID card, it was neither a user or system id card");
        }
    }

    protected DateTime nullableDateTime(XMLGregorianCalendar xmlDate) {
        return xmlDate != null ? new DateTime(xmlDate.toGregorianCalendar(), DateTimeZone.UTC) : null;
    }

    protected List<Delegation> getDelegationsCommon(String delegatorCpr, String delegateeCpr, String delegationId, XMLGregorianCalendar effectiveFrom, XMLGregorianCalendar effectiveTo) {
        auditLogger.log("Hent bemyndigelser", delegateeCpr);

        List<Delegation> delegations = new LinkedList<>();

        // check arguments
        if ((delegatorCpr != null ? 1 : 0) + (delegateeCpr != null ? 1 : 0) + (delegationId != null ? 1 : 0) != 1) {
            throw new IllegalArgumentException("A single argument must be supplied, i.e. exactly one of delegatorCpr, delegateeCpr or delegationId must not be null");
        }

        // authorize
        if (delegatorCpr != null)
            authorizeOperationForCpr("getDelegations", "IDCard CPR was different from DelegatorCpr", delegatorCpr);
        else if (delegateeCpr != null)
            authorizeOperationForCpr("getDelegations", "IDCard CPR was different from DelegateeCpr", delegateeCpr);

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

    protected List<String> deleteDelegationsCommon(String delegatorCpr, String delegateeCpr, List<String> delegationIds, XMLGregorianCalendar xmlDate) {
        auditLogger.log("Slet bemyndigelser", delegateeCpr);

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
            authorizeOperationForCpr("deleteDelegations", "IDCard CPR was different from DelegatorCpr", delegatorCpr);
        else
            authorizeOperationForCpr("deleteDelegations", "IDCard CPR was different from DelegateeCpr", delegateeCpr);

        // invoke manager
        List<String> result = new LinkedList<>();
        for (String delegationId : delegationIds) {
            String deleted = delegationManager.deleteDelegation(delegatorCpr, delegateeCpr, delegationId, deletionDate);
            if (deleted != null) result.add(deleted);
        }
        return result;
    }
}
