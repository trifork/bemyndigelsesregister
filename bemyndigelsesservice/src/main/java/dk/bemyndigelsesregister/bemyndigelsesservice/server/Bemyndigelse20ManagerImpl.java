package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse20;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Status;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/**
 * BEM 2.0 bemyndigelse
 * Created by obj on 02-02-2016.
 */
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
    public Bemyndigelse20 opretAnmodningOmBemyndigelse(String linkedSystemKode, String bemyndigendeCpr, String bemyndigedeCpr, String bemyndigedeCvr, String arbejdsfunktionKode, List<String> rettighedKoder, String systemKode, DateTime gyldigFra, DateTime gyldigTil) {
        Bemyndigelse20 bemyndigelse = createBemyndigelse(linkedSystemKode, bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, rettighedKoder, Status.BESTILT, systemKode, gyldigFra, gyldigTil);

        bemyndigelseDao.save(bemyndigelse);

        return bemyndigelse;
    }

    @Override
    public Collection<Bemyndigelse20> godkendBemyndigelser(Collection<String> bemyndigelsesKoder) {
        Collection<Bemyndigelse20> bemyndigelser = bemyndigelseDao.findByKoder(bemyndigelsesKoder);

        for (Bemyndigelse20 bemyndigelse : bemyndigelser) {
            approveBemyndigelseAndShutdownConflicts(bemyndigelse);
        }
        return bemyndigelser;
    }

    @Override
    public Bemyndigelse20 opretGodkendtBemyndigelse(String linkedSystemKode, String bemyndigendeCpr, String bemyndigedeCpr, String bemyndigedeCvr, String arbejdsfunktionKode, List<String> rettighedKoder, String systemKode, DateTime gyldigFra, DateTime gyldigTil) {
        Bemyndigelse20 bemyndigelse = createBemyndigelse(linkedSystemKode, bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, rettighedKoder, Status.GODKENDT, systemKode, gyldigFra, gyldigTil);

        approveBemyndigelseAndShutdownConflicts(bemyndigelse);

        return bemyndigelse;
    }



    private Bemyndigelse20 createBemyndigelse(String linkedSystemKode, String bemyndigendeCpr, String bemyndigedeCpr, String bemyndigedeCvr, String arbejdsfunktionKode, List<String> rettighedKoder, Status status, String systemKode, DateTime gyldigFra, DateTime gyldigTil) {
        LinkedSystem linkedSystem = linkedSystemDao.findByKode(linkedSystemKode);
        DateTime now = systemService.getDateTime();

        final Bemyndigelse20 bemyndigelse = new Bemyndigelse20();
        bemyndigelse.setKode(systemService.createUUIDString());
        bemyndigelse.setBemyndigendeCpr(bemyndigendeCpr);
        bemyndigelse.setBemyndigedeCpr(bemyndigedeCpr);
        bemyndigelse.setBemyndigedeCvr(bemyndigedeCvr);

        bemyndigelse.setArbejdsfunktionKode(arbejdsfunktionDao.findByKode(linkedSystem, arbejdsfunktionKode).getKode());

        bemyndigelse.setStatus(status);

// TODO OBJ fix        bemyndigelse.setRettighedKode(rettighedDao.findByKode(linkedSystem, rettighedKode).getKode());
        bemyndigelse.setLinkedSystemKode(linkedSystemDao.findByKode(systemKode).getKode());

        final DateTime validFrom = defaultIfNull(gyldigFra, now);
        final DateTime validTo = defaultIfNull(gyldigTil, now.plusYears(100));
        if (!validFrom.isBefore(validTo)) {
            throw new IllegalArgumentException("GyldigFra=" + validFrom + " must be before GyldigTil=" + validTo);
        }
        bemyndigelse.setGyldigFra(validFrom);
        bemyndigelse.setGyldigTil(validTo);

        bemyndigelse.setSidstModificeret(now);
        bemyndigelse.setSidstModificeretAf("Service");

        bemyndigelse.setVersionsid(1);
        return bemyndigelse;
    }


    private void approveBemyndigelseAndShutdownConflicts(Bemyndigelse20 bemyndigelse) {
/* TODO OBJ fix
        final Collection<Bemyndigelse> existingBemyndigelser = bemyndigelseDao.findByInPeriod(
                bemyndigelse.getBemyndigedeCpr(),
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
            bemyndigelseDao.save(existingBemyndigelse);
        }
        bemyndigelse.setStatus(Status.GODKENDT);
        bemyndigelse.setSidstModificeret(systemService.getDateTime());
        bemyndigelse.setGodkendelsesdato(systemService.getDateTime());
        bemyndigelseDao.save(bemyndigelse);
*/
    }
}
