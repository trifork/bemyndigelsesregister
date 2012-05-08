package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BemyndigelseManagerImplTest {
    @Mock BemyndigelseDao bemyndigelseDao;
    @Mock SystemService systemService;
    @Mock ArbejdsfunktionDao arbejdsfunktionDao;
    @Mock RettighedDao rettighedDao;
    @Mock StatusTypeDao statusTypeDao;
    @Mock LinkedSystemDao linkedSystemDao;

    @InjectMocks
    BemyndigelseManagerImpl manager = new BemyndigelseManagerImpl();

    final String kode = "UUID kode";
    final String bemyndigendeCpr = "BemyndigendeCpr";
    final String bemyndigedeCpr = "BemyndigedeCpr";
    final String bemyndigedeCvr = "BemyndigedeCvr";
    final String arbejdsfunktionKode = "Arbejdsfunktion";
    final String rettighedKode = "Rettighedskode";
    final String systemKode = "SystemKode";
    final String statusKode = "StatusKode";
    final DateTime now = new DateTime();

    @Test
    public void canCreateBemyndigelse() throws Exception {
        when(systemService.getDateTime()).thenReturn(now);
        when(systemService.createUUIDString()).thenReturn(kode);
        when(arbejdsfunktionDao.findByArbejdsfunktion(arbejdsfunktionKode)).thenReturn(Arbejdsfunktion.createForTest(arbejdsfunktionKode));
        when(rettighedDao.findByRettighedskode(rettighedKode)).thenReturn(Rettighed.createForTest(rettighedKode));
        when(linkedSystemDao.findBySystem(systemKode)).thenReturn(LinkedSystem.createForTest(systemKode));

        final Bemyndigelse bemyndigelse = manager.opretAnmodningOmBemyndigelse(bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, rettighedKode, systemKode, null, null);

        verify(bemyndigelseDao).save(bemyndigelse);
        assertEquals(kode, bemyndigelse.getKode());
        assertEquals(now, bemyndigelse.getGodkendelsesdato());
        assertEquals(now, bemyndigelse.getGyldigFra());
        assertEquals(now.plusYears(100), bemyndigelse.getGyldigTil());
        assertEquals(bemyndigendeCpr, bemyndigelse.getBemyndigendeCpr());
        assertEquals(bemyndigedeCpr, bemyndigelse.getBemyndigedeCpr());
        assertEquals(bemyndigedeCvr, bemyndigelse.getBemyndigedeCvr());
        assertEquals(arbejdsfunktionKode, bemyndigelse.getArbejdsfunktion().getArbejdsfunktion());
        //TODO: Hvad med status?
        assertEquals(rettighedKode, bemyndigelse.getRettighed().getRettighedskode());
        assertEquals(systemKode, bemyndigelse.getLinkedSystem().getSystem());
    }

    @Test
    public void canCreateBemyndigelseWithValidPeriod() throws Exception {
        DateTime gyldigFra = now.plusDays(3);
        DateTime gyldigTil = now.plusDays(7);
        when(systemService.getDateTime()).thenReturn(now);

        final Bemyndigelse bemyndigelse = manager.opretAnmodningOmBemyndigelse(bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, rettighedKode, systemKode, gyldigFra, gyldigTil);

        assertEquals(now, bemyndigelse.getGodkendelsesdato());
        assertEquals(gyldigFra, bemyndigelse.getGyldigFra());
        assertEquals(gyldigTil, bemyndigelse.getGyldigTil());
    }

    @Test(expected = IllegalArgumentException.class)
    public void gyldigFraMustBeBeforeGyldigTil() throws Exception {
        DateTime gyldigTil = now.minusDays(7);
        when(systemService.getDateTime()).thenReturn(now);

        manager.opretAnmodningOmBemyndigelse(bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, rettighedKode, systemKode, null, gyldigTil);
    }
}
