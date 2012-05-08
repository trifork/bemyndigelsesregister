package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Collection;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@Repository
public class BemyndigelseManagerImpl implements BemyndigelseManager {
    private static Logger logger = Logger.getLogger(BemyndigelseManagerImpl.class);
    @Inject
    BemyndigelseDao bemyndigelseDao;

    @Inject
    SystemService systemService;

    @Inject
    ArbejdsfunktionDao arbejdsfunktionDao;

    @Inject
    RettighedDao rettighedDao;

    @Inject
    StatusTypeDao statusTypeDao;

    @Inject
    LinkedSystemDao linkedSystemDao;

    @Override
    public Bemyndigelse opretAnmodningOmBemyndigelse(String bemyndigendeCpr, String bemyndigedeCpr, String bemyndigedeCvr, String arbejdsfunktionKode, String rettighedKode, String systemKode, DateTime gyldigFra, DateTime gyldigTil) {
        final Bemyndigelse bemyndigelse = createBemyndigelse(bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, rettighedKode, systemKode, gyldigFra, gyldigTil);

        bemyndigelseDao.save(bemyndigelse);

        return bemyndigelse;
    }

    private Bemyndigelse createBemyndigelse(String bemyndigendeCpr, String bemyndigedeCpr, String bemyndigedeCvr, String arbejdsfunktionKode, String rettighedKode, String systemKode, DateTime gyldigFra, DateTime gyldigTil) {
        DateTime now = systemService.getDateTime();

        final Bemyndigelse bemyndigelse = new Bemyndigelse();
        bemyndigelse.setKode(systemService.createUUIDString());
        bemyndigelse.setBemyndigendeCpr(bemyndigendeCpr);
        bemyndigelse.setBemyndigedeCpr(bemyndigedeCpr);
        bemyndigelse.setBemyndigedeCvr(bemyndigedeCvr);

        bemyndigelse.setArbejdsfunktion(arbejdsfunktionDao.findByArbejdsfunktion(arbejdsfunktionKode));

        bemyndigelse.setStatus(statusTypeDao.get(1)); //TODO:

        bemyndigelse.setRettighed(rettighedDao.findByRettighedskode(rettighedKode));
        bemyndigelse.setLinkedSystem(linkedSystemDao.findBySystem(systemKode));

        final DateTime validFrom = defaultIfNull(gyldigFra, now);
        final DateTime validTo = defaultIfNull(gyldigTil, now.plusYears(100));
        if (!validFrom.isBefore(validTo)) {
            throw new IllegalArgumentException("GyldigFra=" + validFrom + " must be before GyldigTil=" + validTo);
        }
        bemyndigelse.setGyldigFra(validFrom);
        bemyndigelse.setGyldigTil(validTo);

        bemyndigelse.setVersionsid(1);
        return bemyndigelse;
    }

    @Override
    public Collection<Bemyndigelse> godkendBemyndigelser(Collection<String> bemyndigelsesKoder) {
        Collection<Bemyndigelse> bemyndigelser = bemyndigelseDao.findByKoder(bemyndigelsesKoder);

        for (Bemyndigelse bemyndigelse : bemyndigelser) {
            approveBemyndigelseAndShutdownConflicts(bemyndigelse);
        }
        return bemyndigelser;
    }

    private void approveBemyndigelseAndShutdownConflicts(Bemyndigelse bemyndigelse) {
        final Collection<Bemyndigelse> existingBemyndigelser = bemyndigelseDao.findByInPeriod(
                bemyndigelse.getBemyndigedeCpr(),
                bemyndigelse.getBemyndigedeCvr(),
                bemyndigelse.getArbejdsfunktion(),
                bemyndigelse.getRettighed(),
                bemyndigelse.getLinkedSystem(),
                bemyndigelse.getGyldigFra(),
                bemyndigelse.getGyldigTil()
        );
        for (Bemyndigelse existingBemyndigelse : existingBemyndigelser) {
            logger.info("Shutting down Bemyndigelse with kode=" + existingBemyndigelse.getKode());
            existingBemyndigelse.setGyldigTil(bemyndigelse.getGyldigFra());
            bemyndigelseDao.save(existingBemyndigelse);
        }

        bemyndigelse.setGodkendelsesdato(systemService.getDateTime());
        bemyndigelseDao.save(bemyndigelse);
    }

    @Override
    public Bemyndigelse opretGodkendtBemyndigelse(String bemyndigendeCpr, String bemyndigedeCpr, String bemyndigedeCvr, String arbejdsfunktionKode, String rettighedKode, String systemKode, DateTime gyldigFra, DateTime gyldigTil) {
        Bemyndigelse bemyndigelse = createBemyndigelse(bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, rettighedKode, systemKode, gyldigFra, gyldigTil);

        approveBemyndigelseAndShutdownConflicts(bemyndigelse);

        return bemyndigelse;
    }
}
