package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Collection;

@Repository
public class BemyndigelseManagerImpl implements BemyndigelseManager {
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
    public Bemyndigelse opretAnmodningOmBemyndigelse(String bemyndigendeCpr, String bemyndigedeCpr, String bemyndigedeCvr, String arbejdsfunktionKode, String rettighedKode, String systemKode) {
        final DateTime now = systemService.getDateTime();

        final Bemyndigelse bemyndigelse = new Bemyndigelse();
        bemyndigelse.setKode(systemService.createUUIDString());
        bemyndigelse.setBemyndigendeCpr(bemyndigendeCpr);
        bemyndigelse.setBemyndigedeCpr(bemyndigedeCpr);
        bemyndigelse.setBemyndigedeCvr(bemyndigedeCvr);

        bemyndigelse.setArbejdsfunktion(arbejdsfunktionDao.findByArbejdsfunktion(arbejdsfunktionKode));

        bemyndigelse.setStatus(statusTypeDao.get(1)); //TODO:

        bemyndigelse.setRettighed(rettighedDao.findByRettighedskode(rettighedKode));
        bemyndigelse.setLinkedSystem(linkedSystemDao.findBySystem(systemKode));

        bemyndigelse.setGodkendelsesdato(now);
        bemyndigelse.setGyldigFra(now);
        bemyndigelse.setGyldigTil(now.plusYears(100));
        bemyndigelse.setVersionsid(1);
        bemyndigelseDao.save(bemyndigelse);

        return bemyndigelse;
    }

    @Override
    public Collection<Bemyndigelse> godkendBemyndigelser(Collection<String> bemyndigelsesKoder) {
        Collection<Bemyndigelse> bemyndigelser = bemyndigelseDao.findByKoder(bemyndigelsesKoder);

        for (Bemyndigelse bemyndigelse : bemyndigelser) {
            //TODO: find alle tilsvarende bemyndigelser??
            bemyndigelse.setGodkendelsesdato(systemService.getDateTime());
            bemyndigelseDao.save(bemyndigelse);
        }
        return bemyndigelser;
    }
}
