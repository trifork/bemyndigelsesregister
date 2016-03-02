package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Permission;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.PermissionDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.RoleDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.SystemDao;
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
    SystemDao systemDao;

    @Override
    public Delegation createDelegation(String systemCode, String delegatorCpr, String delegateeCpr, String delegateeCvr, String roleCode, State state, List<String> permissions, DateTime effectiveFrom, DateTime effectiveTo) {
        DateTime now = systemService.getDateTime();
        final DateTime validFrom = defaultIfNull(effectiveFrom, now);
        final DateTime validTo = defaultIfNull(effectiveTo, now.plusYears(2));

        // Find existing delegations with same key
        logger.debug("Finder eksisterende bemyndigelser for system=" + systemCode + ", delegatorCpr=" + delegateeCpr + ", delegateeCpr=" + delegateeCpr + ", delegateeCvr=" + delegateeCvr + ", roleCode=" + roleCode + ", state=" + state + "validFrom=" + validFrom + ", validTo=" + validTo);
        List<Delegation> existingDelegations = delegationDao.findInPeriod(systemCode, delegatorCpr, delegateeCpr, delegateeCvr, roleCode, state, validFrom, validTo);
        if (existingDelegations != null) {
            for (Delegation delegation : existingDelegations) {

                if (state == State.GODKENDT || state == State.BESTILT && delegation.getState() == State.BESTILT) { // determine if existing should be "closed" - depends on state, approved closes existing approved/requested, but requested only closes existing requested
                    DateTime end = delegation.getEffectiveFrom().isAfter(validFrom) ? delegation.getEffectiveFrom() : validFrom;
                    logger.debug("  Afslutter eksisterende bemyndigelse gyldig " + delegation.getEffectiveFrom() + " - " + delegation.getEffectiveTo() + " til tidspunkt + " + end);

                    // update delegation
                    delegation.setEffectiveTo(end);
                    delegation.setLastModified(now);
                    delegation.setLastModifiedBy("Service");
                    delegation.setVersionsid(delegation.getVersionsid() + 1);
                    delegationDao.save(delegation);
                }
            }
        }

        Delegation delegation = createDelegationObject(systemCode, delegatorCpr, delegateeCpr, delegateeCvr, roleCode, state, permissions, validFrom, validTo);
        logger.debug("Creating delegation " + delegation);
        delegationDao.save(delegation);

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

        if (validTo.isBefore(delegation.getEffectiveFrom()))
            throw new IllegalArgumentException("deletionDate=" + validTo + " must be after EffectiveFrom=" + delegation.getEffectiveFrom());
        if (validTo.isAfter(delegation.getEffectiveTo()))
            throw new IllegalArgumentException("deletionDate=" + validTo + " must be before EffectiveTo=" + delegation.getEffectiveTo());

        // update delegation
        delegation.setEffectiveTo(validTo);
        delegation.setLastModified(systemService.getDateTime());
        delegation.setLastModifiedBy("Service");
        delegation.setVersionsid(delegation.getVersionsid() + 1);

        delegationDao.save(delegation);
        return delegation.getCode();
    }

    private Delegation createDelegationObject(String systemCode, String delegatorCpr, String delegateeCpr, String delegateeCvr, String roleCode, State state, List<String> permissions, DateTime effectiveFrom, DateTime effectiveTo) {
        DateTime now = systemService.getDateTime();
        final DateTime validFrom = defaultIfNull(effectiveFrom, now);
        final DateTime validTo = defaultIfNull(effectiveTo, now.plusYears(2));
        if (!validFrom.isBefore(validTo)) {
            throw new IllegalArgumentException("EffectiveFrom=" + validFrom + " must be before effectiveTo=" + validTo);
        }

        final Delegation delegation = new Delegation();
        delegation.setCode(systemService.createUUIDString());
        delegation.setSystemCode(systemCode);
        delegation.setDelegatorCpr(delegatorCpr);
        delegation.setDelegateeCpr(delegateeCpr);
        delegation.setDelegateeCvr(delegateeCvr);

        delegation.setRoleCode(roleCode);
        delegation.setState(state);

        if (permissions != null && !permissions.isEmpty()) {
            Set<String> permissionCodeSet = new HashSet<>(permissions); // ensures uniqueness
            Set<DelegationPermission> permissionSet = new HashSet<>();
            for (String permission : permissionCodeSet) {
                Permission p = permissionDao.findByCode(systemCode, permission);
                if (p == null)
                    throw new IllegalArgumentException("No permission " + permission + " exists for " + systemCode);

                DelegationPermission dp = new DelegationPermission();
                dp.setDelegation(delegation);
                dp.setPermissionCode(p.getCode());
                permissionSet.add(dp);
            }
            delegation.setDelegationPermissions(permissionSet);
        }

        delegation.setEffectiveFrom(validFrom);
        delegation.setEffectiveTo(validTo);

        delegation.setCreated(now);
        delegation.setLastModified(now);
        delegation.setLastModifiedBy("Service");
        delegation.setVersionsid(1);

        return delegation;
    }
}
