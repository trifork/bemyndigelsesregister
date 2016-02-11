package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.State;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.shared.service.SystemService;
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
    private static Logger logger = Logger.getLogger(BemyndigelseManagerImpl.class);

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
    public Delegation createDelegation(String system, String delegatorCpr, String delegateeCpr, String delegateeCvr, String role, State state, List<String> permissions, DateTime effectiveFrom, DateTime effectiveTo) {

        DateTime now = systemService.getDateTime();
        final DateTime validFrom = defaultIfNull(effectiveFrom, now);
        final DateTime validTo = defaultIfNull(effectiveTo, now.plusYears(2));

        // Find existing delegations with same key
        logger.debug("Finder eksisterende bemyndigelser for system=" + system + ", delegatorCpr=" + delegateeCpr + ", delegateeCpr=" + delegateeCpr + ", delegateeCvr=" + delegateeCvr + ", role=" + role + ", state=" + state + "validFrom=" + validFrom + ", validTo=" + validTo);
        List<Delegation> existingDelegations = delegationDao.findByInPeriod(system, delegatorCpr, delegateeCpr, delegateeCvr, role, state, validFrom, validTo);
        if (existingDelegations != null) {
            for (Delegation delegation : existingDelegations) {
                DateTime end = delegation.getEffectiveFrom().isAfter(validFrom) ? delegation.getEffectiveFrom() : validFrom;
                logger.debug("  Afslutter eksisterende bemyndigelse gyldig " + delegation.getEffectiveFrom() + " - " + delegation.getEffectiveTo() + " til tidspunkt + " + end);

                // update delegation
                delegation.setEffectiveTo(end);
                delegation.setSidstModificeret(now);
                delegation.setSidstModificeretAf("Service");
                delegation.setVersionsid(delegation.getVersionsid() + 1);
                delegationDao.save(delegation);
            }
        }

        Delegation delegation = createDelegationObject(system, delegatorCpr, delegateeCpr, delegateeCvr, role, state, permissions, validFrom, validTo);
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
    public Delegation getDelegation(String delegationId) {
        return delegationDao.findById(delegationId);
    }

    @Override
    public String deleteDelegation(String delegatorCpr, String delegateeCpr, String delegationId, DateTime deletionDate) {
        DateTime now = systemService.getDateTime();
        final DateTime validTo = defaultIfNull(deletionDate, now);

        // find existing delegation
        Delegation delegation = delegationDao.findById(delegationId);

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
        delegation.setSidstModificeret(systemService.getDateTime());
        delegation.setSidstModificeretAf("Service");
        delegation.setVersionsid(delegation.getVersionsid() + 1);

        delegationDao.save(delegation);
        return delegation.getDomainId();
    }

    private Delegation createDelegationObject(String system, String delegatorCpr, String delegateeCpr, String delegateeCvr, String role, State state, List<String> permissions, DateTime effectiveFrom, DateTime effectiveTo) {
        DateTime now = systemService.getDateTime();
        final DateTime validFrom = defaultIfNull(effectiveFrom, now);
        final DateTime validTo = defaultIfNull(effectiveTo, now.plusYears(2));
        if (!validFrom.isBefore(validTo)) {
            throw new IllegalArgumentException("EffectiveFrom=" + validFrom + " must be before effectiveTo=" + validTo);
        }

        final Delegation delegation = new Delegation();
        delegation.setDomainId(systemService.createUUIDString());
        delegation.setDelegatingSystem(system);
        delegation.setDelegatorCpr(delegatorCpr);
        delegation.setDelegateeCpr(delegateeCpr);
        delegation.setDelegateeCvr(delegateeCvr);

        delegation.setRole(role);
        delegation.setState(state);

        if (permissions != null && !permissions.isEmpty()) {
            Set<String> permissionCodeSet = new HashSet<>(permissions); // ensures uniqueness
            Set<DelegationPermission> permissionSet = new HashSet<>();
            for (String permission : permissionCodeSet) {
                Permission p = permissionDao.findByDomainId(system, permission);
                if(p == null)
                    throw new IllegalArgumentException("No permission " + permission + " exists for " + system);

                DelegationPermission dp = new DelegationPermission();
                dp.setDelegation(delegation);
                dp.setPermissionId(p.getDomainId());
                permissionSet.add(dp);
            }
            delegation.setDelegationPermissions(permissionSet);
        }

        delegation.setEffectiveFrom(validFrom);
        delegation.setEffectiveTo(validTo);

        delegation.setCreated(now);
        delegation.setSidstModificeret(now);
        delegation.setSidstModificeretAf("Service");
        delegation.setVersionsid(1);

        return delegation;
    }
}
