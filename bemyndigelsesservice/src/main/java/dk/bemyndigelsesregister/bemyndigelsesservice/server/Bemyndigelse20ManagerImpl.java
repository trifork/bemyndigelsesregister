package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse20;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse20Rettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Status;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ArbejdsfunktionDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.Bemyndigelse20Dao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.LinkedSystemDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.RettighedDao;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
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
public class Bemyndigelse20ManagerImpl implements Bemyndigelse20Manager {
    private static Logger logger = Logger.getLogger(BemyndigelseManagerImpl.class);

    @Inject
    Bemyndigelse20Dao bemyndigelseDao;

    @Inject
    SystemService systemService;

    @Inject
    ArbejdsfunktionDao arbejdsfunktionDao;

    @Inject
    RettighedDao rettighedDao;

    @Inject
    LinkedSystemDao linkedSystemDao;

    @Override
    public Bemyndigelse20 createDelegation(String system, String delegatorCpr, String delegateeCpr, String delegateeCvr, String role, Status state, List<String> permissions, DateTime effectiveFrom, DateTime effectiveTo) {

        DateTime now = systemService.getDateTime();
        final DateTime validFrom = defaultIfNull(effectiveFrom, now);
        final DateTime validTo = defaultIfNull(effectiveTo, now.plusYears(100));

        // Find existing delegations with same key
        logger.debug("Finder eksisterende bemyndigelser for system=" + system + ", delegatorCpr=" + delegateeCpr + ", delegateeCpr=" + delegateeCpr + ", delegateeCvr=" + delegateeCvr + ", role=" + role + ", state=" + state + "validFrom=" + validFrom + ", validTo=" + validTo);
        List<Bemyndigelse20> existingDelegations = bemyndigelseDao.findByInPeriod(system, delegatorCpr, delegateeCpr, delegateeCvr, role, state, validFrom, validTo);
        if (existingDelegations != null) {
            for (Bemyndigelse20 bemyndigelse : existingDelegations) {
                DateTime end = bemyndigelse.getGyldigFra().isAfter(validFrom) ? bemyndigelse.getGyldigFra() : validFrom;
                logger.debug("  Afslutter eksisterende bemyndigelse gyldig " + bemyndigelse.getGyldigFra() + " - " + bemyndigelse.getGyldigTil() + " til tidspunkt + " + end);

                // update delegation
                bemyndigelse.setGyldigTil(end);
                bemyndigelse.setSidstModificeret(now);
                bemyndigelse.setSidstModificeretAf("Service");
                bemyndigelse.setVersionsid(bemyndigelse.getVersionsid() + 1);
                bemyndigelseDao.save(bemyndigelse);
            }
        }

        Bemyndigelse20 bemyndigelse = createDelegationObject(system, delegatorCpr, delegateeCpr, delegateeCvr, role, state, permissions, validFrom, validTo);
        logger.debug("Opretter bemyndigelse" + bemyndigelse);
        bemyndigelseDao.save(bemyndigelse);

        return bemyndigelse;
    }

    @Override
    public List<Bemyndigelse20> getDelegationsByDelegatorCpr(String cpr) {
        return bemyndigelseDao.findByBemyndigendeCpr(cpr);
    }

    @Override
    public List<Bemyndigelse20> getDelegationsByDelegateeCpr(String cpr) {
        return bemyndigelseDao.findByBemyndigedeCpr(cpr);
    }

    @Override
    public Bemyndigelse20 getDelegation(String delegationId) {
        return bemyndigelseDao.findByKode(delegationId);
    }

    @Override
    public String deleteDelegation(String delegationId, DateTime deletionDate) {
        // find existing delegation
        Bemyndigelse20 bemyndigelse = bemyndigelseDao.findByKode(delegationId);

        // validate arguments
        if (deletionDate.isBefore(bemyndigelse.getGyldigFra()))
            throw new IllegalArgumentException("deletetionDate=" + deletionDate + " must be after EffectiveFrom=" + bemyndigelse.getGyldigFra());

        // update delegation
        bemyndigelse.setGyldigTil(deletionDate);
        bemyndigelse.setSidstModificeret(systemService.getDateTime());
        bemyndigelse.setSidstModificeretAf("Service");
        bemyndigelse.setVersionsid(bemyndigelse.getVersionsid() + 1);

        bemyndigelseDao.save(bemyndigelse);
        return bemyndigelse.getKode();
    }

    private Bemyndigelse20 createDelegationObject(String system, String delegatorCpr, String delegateeCpr, String delegateeCvr, String role, Status state, List<String> permissions, DateTime effectiveFrom, DateTime effectiveTo) {
        DateTime now = systemService.getDateTime();
        final DateTime validFrom = defaultIfNull(effectiveFrom, now);
        final DateTime validTo = defaultIfNull(effectiveTo, now.plusYears(100));
        if (!validFrom.isBefore(validTo)) {
            throw new IllegalArgumentException("GyldigFra=" + validFrom + " must be before GyldigTil=" + validTo);
        }

        LinkedSystem linkedSystem = linkedSystemDao.findByKode(system);
        final Bemyndigelse20 bemyndigelse = new Bemyndigelse20();
        bemyndigelse.setKode(systemService.createUUIDString());
        bemyndigelse.setLinkedSystemKode(system);
        bemyndigelse.setBemyndigendeCpr(delegatorCpr);
        bemyndigelse.setBemyndigedeCpr(delegateeCpr);
        bemyndigelse.setBemyndigedeCvr(delegateeCvr);

        bemyndigelse.setArbejdsfunktionKode(arbejdsfunktionDao.findByKode(linkedSystem, role).getKode());
        bemyndigelse.setStatus(state);
        if(state == Status.GODKENDT)
            bemyndigelse.setGodkendelsesdato(now);

        if (permissions != null && !permissions.isEmpty()) {
            Set<String> permissionCodeSet = new HashSet<>(permissions); // ensures uniqueness
            Set<Bemyndigelse20Rettighed> permissionSet = new HashSet<>();
            for (String permission : permissionCodeSet) {
                Bemyndigelse20Rettighed rettighed = new Bemyndigelse20Rettighed();
                rettighed.setBemyndigelse(bemyndigelse);
                rettighed.setRettighedKode(rettighedDao.findByKode(linkedSystem, permission).getKode());

                permissionSet.add(rettighed);
            }
            bemyndigelse.setRettigheder(permissionSet);
        }

        bemyndigelse.setGyldigFra(validFrom);
        bemyndigelse.setGyldigTil(validTo);

        bemyndigelse.setSidstModificeret(now);
        bemyndigelse.setSidstModificeretAf("Service");
        bemyndigelse.setVersionsid(1);

        return bemyndigelse;
    }
}
