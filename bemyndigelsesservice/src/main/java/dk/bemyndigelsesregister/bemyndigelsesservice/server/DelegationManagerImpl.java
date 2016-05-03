package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.bemyndigelse._2016._01._01.State;
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
 * Created by obj on 02-02-2016.
 */
@Repository
public class DelegationManagerImpl implements DelegationManager {
    private static Logger logger = Logger.getLogger(DelegationManagerImpl.class);

    @Inject
    DelegationDao delegationDao;

    @Inject
    SystemService systemService;

    @Inject
    RoleDao roleDao;

    @Inject
    PermissionDao permissionDao;

    @Inject
    DelegatablePermissionDao delegatablePermissionDao;

    @Inject
    SystemDao systemDao;

    @Override
    public Delegation createDelegation(String systemCode, String delegatorCpr, String delegateeCpr, String delegateeCvr, String roleCode, State state, List<String> permissionCodes, DateTime effectiveFrom, DateTime effectiveTo) {
        logger.info("createDelegation started, system=[" + systemCode + "], delegatorCpr=[" + delegatorCpr + "], delegateeCpr=[" + delegateeCpr + "], delegateeCvr=[" + delegateeCvr + "], roleCode=[" + roleCode + "], state=[" + state + "], efftiveFrom=[" + effectiveFrom + "], effectiveTo=[" + effectiveTo + "]");

        DateTime now = systemService.getDateTime();
        DateTime validFrom = defaultIfNull(effectiveFrom, now);

        DateTime twoYearsFromStart = validFrom.plusYears(2);
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
                if (state == State.GODKENDT || state == State.ANMODET && delegation.getState() == State.ANMODET) { // determine if existing should be "closed" - depends on state, approved closes existing approved/requested, but requested only closes existing requested
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
        return delegationDao.findByDelegatorCpr(cpr);
    }

    @Override
    public List<Delegation> getDelegationsByDelegateeCpr(String cpr) {
        return delegationDao.findByDelegateeCpr(cpr);
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

    private Delegation createDelegationObject(String systemCode, String delegatorCpr, String delegateeCpr, String delegateeCvr, String roleCode, State state, List<String> permissionCodes, DateTime effectiveFrom, DateTime effectiveTo) {
        DateTime now = systemService.getDateTime();

        if (!effectiveFrom.isBefore(effectiveTo)) {
            throw new IllegalArgumentException("EffectiveFrom=[" + effectiveFrom + "] must be before effectiveTo=[" + effectiveTo + "]");
        }

        DelegatingSystem system = systemDao.findByCode(systemCode);
        if (system == null)
            throw new IllegalArgumentException("No system [" + systemCode + "] exists");

        Role role = roleDao.findByCode(system.getId(), roleCode);
        if (role == null)
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

            if (permissionCodeSet.contains(Metadata.ASTERISK_PERMISSION_CODE) && permissionCodeSet.size() > 1) {
                // if delegation contains asterisk permission, delete other permissions
                logger.debug("  Truncating permissions " + permissionCodeSet + " to *");
                permissionCodeSet.clear();
                permissionCodeSet.add(Metadata.ASTERISK_PERMISSION_CODE);
            }

            Set<DelegationPermission> permissionSet = new HashSet<>();
            for (String permissionCode : permissionCodeSet) {
                Permission permission = permissionDao.findByCode(systemCode, permissionCode);
                if (permission == null)
                    throw new IllegalArgumentException("No permission [" + permissionCode + "] exists for system [" + systemCode + "]");

                DelegatablePermission delegatablePermission = delegatablePermissionDao.findByPermissionAndRole(permission.getId(), role.getId());
                if (delegatablePermission == null || !delegatablePermission.isDelegatable())
                    throw new IllegalArgumentException("Permission [" + permissionCode + "] is not delegatable for role [" + roleCode + "]");

                DelegationPermission dp = new DelegationPermission();
                dp.setDelegation(delegation);
                dp.setPermissionCode(permission.getCode());
                dp.setLastModified(now);
                dp.setLastModifiedBy(getClass().getSimpleName());
                permissionSet.add(dp);

                logger.debug("  + permision [" + permission.getCode() + "]");
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
