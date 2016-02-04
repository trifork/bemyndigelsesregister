package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.State;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean.SystemDao;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/**
 * BEM 2.0 bemyndigelse
 * Created by obj on 02-02-2016.
 */
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


    private void saveDelegationAndEndConflictingDelegations(Delegation delegation) {
/* TODO OBJ fix
        final Collection<Bemyndigelse> existingBemyndigelser = permissionDao.findByInPeriod(
                bemyndigelse.getDelegateeCpr(),
                bemyndigelse.getBemyndigedeCvr(),
                bemyndigelse.getArbejdsfunktionKode(),
                bemyndigelse.getRettighedKode(),
                bemyndigelse.getLinkedSystemKode(),
                bemyndigelse.getGyldigFra(),
                bemyndigelse.getGyldigTil()
        );
        for (Bemyndigelse existingBemyndigelse : existingBemyndigelser) {
            logger.info("Shutting down Bemyndigelse with kode=" + existingBemyndigelse.getKode());
            existingBemyndigelse.setGyldigTil(bemyndigelse.getGyldigFra());
            existingBemyndigelse.setSidstModificeret(systemService.getDateTime());
            permissionDao.save(existingBemyndigelse);
        }
        bemyndigelse.setStatus(Status.GODKENDT);
        */
        delegation.setSidstModificeret(systemService.getDateTime());
        delegation.setCreated(systemService.getDateTime());
        delegationDao.save(delegation);
    }


    @Override
    public Delegation createDelegations(String delegatorCpr, String delegateeCpr, String delegateeCvr, String roleId, String state, String systemId, List<String> permissionIds, DateTime effectiveFrom, DateTime effectiveTo) {
        DelegatingSystem delegatingSystem = systemDao.findById(systemId);
        DateTime now = systemService.getDateTime();

        final Delegation delegation = new Delegation();
        delegation.setDelegationId(systemService.createUUIDString());
        delegation.setDelegatorCpr(delegatorCpr);
        delegation.setDelegateeCpr(delegateeCpr);
        delegation.setDelegateeCpr(delegateeCvr);
        delegation.setRole(roleDao.findById(systemId, roleId));
        delegation.setState(State.valueOf(state.toUpperCase()));
        delegation.setDelegatingSystem(delegatingSystem);
        Set<DelegationPermission> permissions = delegation.getPermissions();
        for (String permissionId : permissionIds) {
            DelegationPermission permission = new DelegationPermission();
            permission.setPermissionId(permissionId);
            // TODO? permission.setDelegation(delegation);
        }
        final DateTime validFrom = defaultIfNull(effectiveFrom, now);
        final DateTime validTo = defaultIfNull(effectiveTo, now.plusYears(2));
        if (!validFrom.isBefore(validTo)) {
            throw new IllegalArgumentException("EffectiveFrom=" + validFrom + " must be before EffectiveTo=" + validTo);
        }
        delegation.setEffectiveFrom(validFrom);
        delegation.setEffectiveTo(validTo);
        delegation.setSidstModificeret(now);
        delegation.setSidstModificeretAf("Service");
        delegation.setVersionsid(1);
        saveDelegationAndEndConflictingDelegations(delegation);

        return delegation;
    }

}
