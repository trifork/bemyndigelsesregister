package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.State;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
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
    LinkedSystemDao linkedSystemDao;

    @Override
    public Delegation createDelegation(String system, String delegatorCpr, String delegateeCpr, String delegateeCvr, String role, State state, List<String> permissions, DateTime effectiveFrom, DateTime effectiveTo) {

        DateTime now = systemService.getDateTime();
        final DateTime validFrom = defaultIfNull(effectiveFrom, now);
        final DateTime validTo = defaultIfNull(effectiveTo, now.plusYears(100));

        // Find existing delegations with same key
        logger.debug("Finder eksisterende bemyndigelser for system=" + system + ", delegatorCpr=" + delegateeCpr + ", delegateeCpr=" + delegateeCpr + ", delegateeCvr=" + delegateeCvr + ", role=" + role + ", state=" + state + "validFrom=" + validFrom + ", validTo=" + validTo);
        List<Delegation> existingDelegations = delegationDao.findByInPeriod(system, delegatorCpr, delegateeCpr, delegateeCvr, role, state, validFrom, validTo);
        if (existingDelegations != null) {
            for (Delegation bemyndigelse : existingDelegations) {
                DateTime end = bemyndigelse.getEffectiveFrom().isAfter(validFrom) ? bemyndigelse.getEffectiveFrom() : validFrom;
                logger.debug("  Afslutter eksisterende bemyndigelse gyldig " + bemyndigelse.getEffectiveFrom() + " - " + bemyndigelse.getEffectiveTo() + " til tidspunkt + " + end);

                // update delegation
                bemyndigelse.setEffectiveTo(end);
                bemyndigelse.setSidstModificeret(now);
                bemyndigelse.setSidstModificeretAf("Service");
                bemyndigelse.setVersionsid(bemyndigelse.getVersionsid() + 1);
                delegationDao.save(bemyndigelse);
            }
        }

        Delegation bemyndigelse = createDelegationObject(system, delegatorCpr, delegateeCpr, delegateeCvr, role, state, permissions, validFrom, validTo);
        logger.debug("Opretter bemyndigelse" + bemyndigelse);
        delegationDao.save(bemyndigelse);

        return bemyndigelse;
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
    public String deleteDelegation(String delegationId, DateTime deletionDate) {
        // find existing delegation
        Delegation bemyndigelse = delegationDao.findById(delegationId);

        // validate arguments
        if (deletionDate.isBefore(bemyndigelse.getEffectiveFrom()))
            throw new IllegalArgumentException("deletionDate=" + deletionDate + " must be after EffectiveFrom=" + bemyndigelse.getEffectiveFrom());

        // update delegation
        bemyndigelse.setEffectiveTo(deletionDate);
        bemyndigelse.setSidstModificeret(systemService.getDateTime());
        bemyndigelse.setSidstModificeretAf("Service");
        bemyndigelse.setVersionsid(bemyndigelse.getVersionsid() + 1);

        delegationDao.save(bemyndigelse);
        return bemyndigelse.getKode();
    }

    private Delegation createDelegationObject(String system, String delegatorCpr, String delegateeCpr, String delegateeCvr, String role, State state, List<String> permissions, DateTime effectiveFrom, DateTime effectiveTo) {
        DateTime now = systemService.getDateTime();
        final DateTime validFrom = defaultIfNull(effectiveFrom, now);
        final DateTime validTo = defaultIfNull(effectiveTo, now.plusYears(100));
        if (!validFrom.isBefore(validTo)) {
            throw new IllegalArgumentException("EffectiveFrom=" + validFrom + " must be before effectiveTo=" + validTo);
        }

        DelegatingSystem delegatingSystem = null; // linkedSystemDao.findByKode(system); TODO OBJ: Kari, mangler der en delegatingSystemDao, eller hvad?
        final Delegation delegation = new Delegation();
        delegation.setKode(systemService.createUUIDString());
        delegation.setDelegatingSystem(delegatingSystem);
        delegation.setDelegatorCpr(delegatorCpr);
        delegation.setDelegateeCpr(delegateeCpr);
        delegation.setDelegateeCvr(delegateeCvr);

        delegation.setRole(roleDao.findById(system, role));
        delegation.setState(state);

        if (permissions != null && !permissions.isEmpty()) {
            Set<String> permissionCodeSet = new HashSet<>(permissions); // ensures uniqueness
            Set<DelegationPermission> permissionSet = new HashSet<>();
            for (String permission : permissionCodeSet) {
                DelegationPermission dp = new DelegationPermission();
                dp.setDelegation(delegation);
                dp.setPermissionId(permissionDao.findById(delegatingSystem, permission).getKode());

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
