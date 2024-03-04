package dk.bemyndigelsesregister.service;

import dk.bemyndigelsesregister.dao.DelegationDAO;
import dk.bemyndigelsesregister.dao.DelegationPermissionDAO;
import dk.bemyndigelsesregister.domain.*;
import dk.bemyndigelsesregister.util.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class DelegationManagerImpl implements DelegationManager {
    private static Logger logger = LogManager.getLogger(DelegationManagerImpl.class);

    @Autowired
    private DelegationDAO delegationDAO;

    @Autowired
    private DelegationPermissionDAO delegationPermissionDAO;

    @Autowired
    private SystemService systemService;

    @Autowired
    private MetadataManager metadataManager;

    @PostConstruct
    private void init() {
        logger.info("DelegationManager initialised");
    }

    @Override
    @Transactional
    public Delegation createDelegation(String systemCode, String delegatorCpr, String delegateeCpr, String delegateeCvr, String roleCode, Status state, List<String> permissionCodes, Instant effectiveFrom, Instant effectiveTo) {
        logger.info("createDelegation started, system=[" + systemCode + "], delegatorCpr=[" + delegatorCpr + "], delegateeCpr=[" + delegateeCpr + "], delegateeCvr=[" + delegateeCvr + "], roleCode=[" + roleCode + "], state=[" + state + "], efftiveFrom=[" + effectiveFrom + "], effectiveTo=[" + effectiveTo + "]");

        Instant now = Instant.now();
        Instant validFrom = defaultIfNull(effectiveFrom, now);

        Instant twoYearsFromStart = validFrom.isBefore(now) ? DateUtils.plysYears(now, 2) : DateUtils.plysYears(validFrom, 2);
        Instant validTo = defaultIfNull(effectiveTo, twoYearsFromStart);
        if (validTo.isAfter(twoYearsFromStart)) {
            logger.debug("  changed enddate " + validTo + " to 2 years after start: " + twoYearsFromStart);
            validTo = twoYearsFromStart;
        }

        // Find existing delegations with same key
        List<Delegation> existingDelegations = delegationDAO.findInPeriod(systemCode, delegatorCpr, delegateeCpr, delegateeCvr, roleCode, validFrom, validTo);
        if (existingDelegations != null) {
            logger.debug("  " + existingDelegations.size() + " overlapping delegations found");

            for (Delegation delegation : existingDelegations) {
                if (state == Status.GODKENDT || state == Status.ANMODET && delegation.getState() == Status.ANMODET) { // determine if existing should be "closed" - depends on state, approved closes existing approved/requested, but requested only closes existing requested
                    Instant end = validFrom; // delegation.getEffectiveFrom().isAfter(validFrom) ? delegation.getEffectiveFrom() : validFrom;

                    logger.debug("  Updating delegation [" + delegation + "] currently valid [" + delegation.getEffectiveFrom() + "]-[" + delegation.getEffectiveTo() + "] to end at [" + end + "]");

                    // update delegation
                    delegation.setEffectiveTo(end);
                    delegation.setVersionsid(delegation.getVersionsid() + 1);
                    delegationDAO.save(delegation);
                }
            }
        }

        Delegation delegation = createDelegationObject(systemCode, delegatorCpr, delegateeCpr, delegateeCvr, roleCode, state, permissionCodes, validFrom, validTo);
        delegationDAO.save(delegation);
        for (DelegationPermission delegationPermission : delegation.getDelegationPermissions()) {
            delegationPermission.setDelegationId(delegation.getId());
            delegationPermissionDAO.save(delegationPermission);
        }

        logger.debug("createdDelegation ended, created [" + delegation + "]");
        return delegation;
    }

    @Override
    public List<Delegation> getDelegationsByDelegatorCpr(String cpr) {
        return getDelegationsByDelegatorCpr(cpr, null, null);
    }

    @Override
    public List<Delegation> getDelegationsByDelegatorCpr(String cpr, Instant effectiveFrom, Instant effectiveTo) {
        logger.debug("  Get delegations [" + cpr + "] currently valid [" + effectiveFrom + "]-[" + effectiveTo + "]");
        return loadPermissions(delegationDAO.findByDelegatorCpr(cpr, effectiveFrom, effectiveTo));
    }

    @Override
    public List<Delegation> getDelegationsByDelegateeCpr(String cpr) {
        return getDelegationsByDelegateeCpr(cpr, null, null);
    }

    @Override
    public List<Delegation> getDelegationsByDelegateeCpr(String cpr, Instant effectiveFrom, Instant effectiveTo) {
        return loadPermissions(delegationDAO.findByDelegateeCpr(cpr, effectiveFrom, effectiveTo));
    }

    @Override
    public Delegation getDelegation(long id) {
        return loadPermissions(delegationDAO.get(id));
    }

    @Override
    public Delegation getDelegation(String delegationCode) {
        return loadPermissions(delegationDAO.findByCode(delegationCode));
    }

    @Override
    @Transactional
    public String deleteDelegation(String delegatorCpr, String delegateeCpr, String delegationCode, Instant deletionDate) {
        Instant now = Instant.now();
        final Instant validTo = defaultIfNull(deletionDate, now);

        // find existing delegation
        Delegation delegation = delegationDAO.findByCode(delegationCode);

        // validate arguments
        if (delegation == null) return null;
        if (delegatorCpr != null && !delegatorCpr.equals(delegation.getDelegatorCpr())) return null;
        if (delegateeCpr != null && !delegateeCpr.equals(delegation.getDelegateeCpr())) return null;

        if (validTo.isAfter(delegation.getEffectiveTo()))
            throw new IllegalArgumentException("deletionDate=[" + validTo + "] must be before EffectiveTo=" + delegation.getEffectiveTo());

        // update delegation
        delegation.setEffectiveTo(validTo);
        if (validTo.isBefore(delegation.getEffectiveFrom())) {
            delegation.setEffectiveFrom(validTo); // BEM-80 - ensures that delegation does not end up with validFrom after ValidTo
        }
        delegation.setVersionsid(delegation.getVersionsid() + 1);

        delegationDAO.save(delegation);
        return delegation.getCode();
    }

    @Override
    public ExpirationInfo getExpirationInfo(String delegatorCpr, int days) {
        return delegationDAO.getExpirationInfo(delegatorCpr, days);
    }

    @Override
    @Transactional
    public int cleanup(Instant beforeDate, int maxRecords) {
        List<Long> ids = delegationDAO.findExpiredBefore(beforeDate, maxRecords);
        for (Long id : ids) {
            delegationPermissionDAO.removeByDelegationId(id);
            delegationDAO.remove(id);
        }

        return ids.size();
    }

    private Delegation createDelegationObject(String systemCode, String delegatorCpr, String delegateeCpr, String delegateeCvr, String roleCode, Status state, List<String> permissionCodes, Instant effectiveFrom, Instant effectiveTo) {
        Instant now = Instant.now();

        if (!effectiveFrom.isBefore(effectiveTo)) {
            throw new IllegalArgumentException("EffectiveFrom=[" + effectiveFrom + "] must be before effectiveTo=[" + effectiveTo + "]");
        }

        Metadata metadata = metadataManager.getMetadata(Domain.DEFAULT_DOMAIN, systemCode);
        if (metadata == null) {
            throw new IllegalArgumentException("No system [" + systemCode + "] exists");
        }

        if (!metadata.containsRole(roleCode)) {
            throw new IllegalArgumentException("No role [" + roleCode + "] exists for system [" + systemCode + "]");
        }

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
                for (DelegatablePermission dp : metadata.getDelegatablePermissions(roleCode)) {
                    if (dp.isDelegatable()) {
                        permissionCodeSet.add(dp.getPermission().getCode());
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
                dp.setDelegationId(delegation.getId());
                dp.setPermissionCode(permissionCode);
                dp.setCode(systemService.createUUIDString());
                permissionSet.add(dp);

                logger.debug("  + permision [" + permissionCode + "]");
            }
            delegation.setDelegationPermissions(permissionSet);
        }

        delegation.setEffectiveFrom(effectiveFrom);
        delegation.setEffectiveTo(effectiveTo);

        delegation.setCreated(now);
        delegation.setVersionsid(1);

        return delegation;
    }

    private Instant defaultIfNull(Instant value, Instant defaultValue) {
        return value != null ? value : defaultValue;
    }

    private List<Delegation> loadPermissions(List<Delegation> delegations) {
        for (Delegation delegation : delegations) {
            loadPermissions(delegation);
        }
        return delegations;
    }

    private Delegation loadPermissions(Delegation delegation) {
        delegation.setDelegationPermissions(new HashSet<>(delegationPermissionDAO.findByDelegationId(delegation.getId())));
        return delegation;
    }
}
