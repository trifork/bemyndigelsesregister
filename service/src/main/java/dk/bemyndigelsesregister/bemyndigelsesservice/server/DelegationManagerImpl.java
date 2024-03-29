package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegatablePermissionDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationDao;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/**
 * BEM 2.0 bemyndigelse
 */
@Repository
public class DelegationManagerImpl implements DelegationManager {
    private static Logger logger = Logger.getLogger(DelegationManagerImpl.class);

    @Inject
    DelegationDao delegationDao;

    @Inject
    SystemService systemService;

    @Inject
    DelegatablePermissionDao delegatablePermissionDao;

    @Inject
    MetadataManager metadataManager;

    @Override
    public Delegation createDelegation(String systemCode, String delegatorCpr, String delegateeCpr, String delegateeCvr, String roleCode, Status state, List<String> permissionCodes, DateTime effectiveFrom, DateTime effectiveTo) {
        logger.info("createDelegation started, system=[" + systemCode + "], delegatorCpr=[" + delegatorCpr + "], delegateeCpr=[" + delegateeCpr + "], delegateeCvr=[" + delegateeCvr + "], roleCode=[" + roleCode + "], state=[" + state + "], efftiveFrom=[" + effectiveFrom + "], effectiveTo=[" + effectiveTo + "]");

        DateTime now = systemService.getDateTime();
        DateTime validFrom = defaultIfNull(effectiveFrom, now);

        DateTime twoYearsFromStart = validFrom.isBefore(now) ? now.plusYears(2) : validFrom.plusYears(2);
        DateTime validTo = defaultIfNull(effectiveTo, twoYearsFromStart);
        if (validTo.isAfter(twoYearsFromStart)) {
            logger.debug("  changed enddate " + validTo + " to 2 years after start: " + twoYearsFromStart);
            validTo = twoYearsFromStart;
        }

        // Find existing delegations with same key
        List<Delegation> existingDelegations = delegationDao.findInPeriod(systemCode, delegatorCpr, delegateeCpr, delegateeCvr, roleCode, state, validFrom, validTo);
        if (existingDelegations != null) {
            logger.debug("  " + existingDelegations.size() + " overlapping delegations found");

            for (Delegation delegation : existingDelegations) {
                if (state == Status.GODKENDT || state == Status.ANMODET && delegation.getState() == Status.ANMODET) { // determine if existing should be "closed" - depends on state, approved closes existing approved/requested, but requested only closes existing requested
                    DateTime end = validFrom; // delegation.getEffectiveFrom().isAfter(validFrom) ? delegation.getEffectiveFrom() : validFrom;

                    logger.debug("  Updating delegation [" + delegation + "] currently valid [" + delegation.getEffectiveFrom() + "]-[" + delegation.getEffectiveTo() + "] to end at [" + end + "]");

                    // update delegation
                    delegation.setEffectiveTo(end);
                    delegation.setLastModified(now);
                    delegation.setLastModifiedBy(getClass().getSimpleName());
                    delegation.setVersionsid(delegation.getVersionsid() + 1);
                    delegationDao.save(delegation);
                }
            }
        }

        Delegation delegation = createDelegationObject(systemCode, delegatorCpr, delegateeCpr, delegateeCvr, roleCode, state, permissionCodes, validFrom, validTo);
        delegationDao.save(delegation);

        logger.debug("createdDelegation ended, created [" + delegation + "]");
        return delegation;
    }

    @Override
    public List<Delegation> getDelegationsByDelegatorCpr(String cpr) {
        return getDelegationsByDelegatorCpr(cpr, null, null);
    }

    @Override
    public List<Delegation> getDelegationsByDelegatorCpr(String cpr, DateTime effectiveFrom, DateTime effectiveTo) {
        logger.debug("  Get delegations [" + cpr + "] currently valid [" + effectiveFrom + "]-[" + effectiveTo + "]");
        return delegationDao.findByDelegatorCpr(cpr, effectiveFrom, effectiveTo);
    }

    @Override
    public List<Delegation> getDelegationsByDelegateeCpr(String cpr) {
        return getDelegationsByDelegateeCpr(cpr, null, null);
    }

    @Override
    public List<Delegation> getDelegationsByDelegateeCpr(String cpr, DateTime effectiveFrom, DateTime effectiveTo) {
        return delegationDao.findByDelegateeCpr(cpr, effectiveFrom, effectiveTo);
    }

    @Override
    public Delegation getDelegation(String delegationCode) {
        return delegationDao.findByCode(delegationCode);
    }

    @Override
    public String deleteDelegation(String delegatorCpr, String delegateeCpr, String delegationCode, DateTime deletionDate) {
        DateTime now = systemService.getDateTime();
        final DateTime validTo = defaultIfNull(deletionDate, now);

        // find existing delegation
        Delegation delegation = delegationDao.findByCode(delegationCode);

        // validate arguments
        if (delegation == null) return null;
        if (delegatorCpr != null && !delegatorCpr.equals(delegation.getDelegatorCpr())) return null;
        if (delegateeCpr != null && !delegateeCpr.equals(delegation.getDelegateeCpr())) return null;

        if (validTo.isAfter(delegation.getEffectiveTo()))
            throw new IllegalArgumentException("deletionDate=[" + validTo + "] must be before EffectiveTo=" + delegation.getEffectiveTo());

        // update delegation
        delegation.setEffectiveTo(validTo);
        delegation.setLastModified(systemService.getDateTime());
        delegation.setLastModifiedBy(getClass().getSimpleName());
        delegation.setVersionsid(delegation.getVersionsid() + 1);

        delegationDao.save(delegation);
        return delegation.getCode();
    }

    @Override
    public ExpirationInfo getExpirationInfo(String delegatorCpr, int days) {
        return delegationDao.getExpirationInfo(delegatorCpr, days);
    }

    private Delegation createDelegationObject(String systemCode, String delegatorCpr, String delegateeCpr, String delegateeCvr, String roleCode, Status state, List<String> permissionCodes, DateTime effectiveFrom, DateTime effectiveTo) {
        DateTime now = systemService.getDateTime();

        if (!effectiveFrom.isBefore(effectiveTo)) {
            throw new IllegalArgumentException("EffectiveFrom=[" + effectiveFrom + "] must be before effectiveTo=[" + effectiveTo + "]");
        }

        Metadata metadata = metadataManager.getMetadata(null, systemCode);
        if (metadata == null)
            throw new IllegalArgumentException("No system [" + systemCode + "] exists");

        if (!metadata.containsRole(roleCode))
            throw new IllegalArgumentException("No role [" + roleCode + "] exists for system [" + systemCode + "]");

        final Delegation delegation = new Delegation();
        delegation.setCode(systemService.createUUIDString());
        delegation.setSystemCode(systemCode);
        delegation.setDelegatorCpr(delegatorCpr);
        delegation.setDelegateeCpr(delegateeCpr);
        delegation.setDelegateeCvr(delegateeCvr);

        delegation.setRoleCode(roleCode);
        delegation.setState(state);

        if (permissionCodes != null && !permissionCodes.isEmpty()) {
            Set<String> permissionCodeSet = new HashSet<>(permissionCodes); // ensures uniqueness

            if (permissionCodeSet.contains(Metadata.ASTERISK_PERMISSION_CODE)) { // if delegation contains *, add any missing permissions
                logger.debug("  * found, expanding permissions");
                for (Metadata.DelegatablePermission dp : metadata.getDelegatablePermissions(roleCode)) {
                    if (dp.isDelegatable()) {
                        permissionCodeSet.add(dp.getPermissionCode());
                    }
                }
            }

            Set<DelegationPermission> permissionSet = new HashSet<>();
            for (String permissionCode : permissionCodeSet) {
                if (!metadata.containsPermission(permissionCode))
                    throw new IllegalArgumentException("No permission [" + permissionCode + "] exists for system [" + systemCode + "]");

                if (!metadata.containsDelegatablePermission(roleCode, permissionCode, true))
                    throw new IllegalArgumentException("Permission [" + permissionCode + "] is not delegatable for role [" + roleCode + "]");

                DelegationPermission dp = new DelegationPermission();
                dp.setDelegation(delegation);
                dp.setPermissionCode(permissionCode);
                dp.setCode(systemService.createUUIDString());
                dp.setLastModified(now);
                dp.setLastModifiedBy(getClass().getSimpleName());
                permissionSet.add(dp);

                logger.debug("  + permision [" + permissionCode + "]");
            }
            delegation.setDelegationPermissions(permissionSet);
        }

        delegation.setEffectiveFrom(effectiveFrom);
        delegation.setEffectiveTo(effectiveTo);

        delegation.setCreated(now);
        delegation.setLastModified(now);
        delegation.setLastModifiedBy(getClass().getSimpleName());
        delegation.setVersionsid(1);

        return delegation;
    }
}
