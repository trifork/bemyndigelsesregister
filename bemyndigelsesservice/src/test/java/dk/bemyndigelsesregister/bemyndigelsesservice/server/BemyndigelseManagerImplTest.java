package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BemyndigelseManagerImplTest {
    @Mock
    BemyndigelseDao bemyndigelseDao;
    @Mock
    SystemService systemService;
    @Mock
    ArbejdsfunktionDao arbejdsfunktionDao;
    @Mock
    RettighedDao rettighedDao;
    @Mock
    StatusTypeDao statusTypeDao;
    @Mock
    LinkedSystemDao linkedSystemDao;

    @InjectMocks
    BemyndigelseManagerImpl manager = new BemyndigelseManagerImpl();

    final String kode = "UUID kode";
    final String bemyndigendeCpr = "BemyndigendeCpr";
    final String bemyndigedeCpr = "BemyndigedeCpr";
    final String bemyndigedeCvr = "BemyndigedeCvr";
    final String arbejdsfunktionKode = "Arbejdsfunktion";
    final Arbejdsfunktion arbejdsfunktion = new Arbejdsfunktion();
    final String rettighedKode = "Rettighedskode";
    final Rettighed rettighed = new Rettighed();
    final String systemKode = "SystemKode";
    final LinkedSystem linkedSystem = new LinkedSystem();
    //final String statusKode = "StatusKode";
    final DateTime now = new DateTime();

    @Test
    public void canCreateBemyndigelse() throws Exception {
        when(systemService.getDateTime()).thenReturn(now);
        when(systemService.createUUIDString()).thenReturn(kode);
        when(arbejdsfunktionDao.findByKode(arbejdsfunktionKode)).thenReturn(Arbejdsfunktion.createForTest(arbejdsfunktionKode));
        when(rettighedDao.findByKode(rettighedKode)).thenReturn(Rettighed.createForTest(rettighedKode));
        when(linkedSystemDao.findByKode(systemKode)).thenReturn(LinkedSystem.createForTest(systemKode));

        final Bemyndigelse bemyndigelse = manager.opretAnmodningOmBemyndigelse(bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, rettighedKode, systemKode, null, null);

        verify(bemyndigelseDao).save(bemyndigelse);
        assertNull(bemyndigelse.getGodkendelsesdato());

        verifyAllFields(bemyndigelse);
    }

    @Test
    public void canCreateBemyndigelseWithValidPeriod() throws Exception {
        DateTime gyldigFra = now.plusDays(3);
        DateTime gyldigTil = now.plusDays(7);
        when(systemService.getDateTime()).thenReturn(now);

        final Bemyndigelse bemyndigelse = manager.opretAnmodningOmBemyndigelse(bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, rettighedKode, systemKode, gyldigFra, gyldigTil);

        assertEquals(gyldigFra, bemyndigelse.getGyldigFra());
        assertEquals(gyldigTil, bemyndigelse.getGyldigTil());
    }

    @Test(expected = IllegalArgumentException.class)
    public void gyldigFraMustBeBeforeGyldigTil() throws Exception {
        DateTime gyldigTil = now.minusDays(7);
        when(systemService.getDateTime()).thenReturn(now);

        manager.opretAnmodningOmBemyndigelse(bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, rettighedKode, systemKode, null, gyldigTil);
    }

    @Test
    public void canGodkendeBemyndigelse() throws Exception {
        final Bemyndigelse bemyndigelse = new Bemyndigelse();

        when(bemyndigelseDao.findByKoder(singletonList(kode))).thenReturn(singletonList(bemyndigelse));
        when(systemService.getDateTime()).thenReturn(now);

        manager.godkendBemyndigelser(singletonList(kode));

        verify(bemyndigelseDao).save(bemyndigelse);
        assertEquals(now, bemyndigelse.getGodkendelsesdato());
    }

    @Test
    public void willShutdownOtherBemyndigelserOnGodkend() throws Exception {
        final DateTime gyldigFra = now;
        final DateTime gyldigTil = now.plusYears(100);
        final Bemyndigelse bemyndigelse = new Bemyndigelse() {{
            setKode(kode);
            setBemyndigendeCpr(bemyndigendeCpr);
            setBemyndigedeCpr(bemyndigedeCpr);
            setBemyndigedeCvr(bemyndigedeCvr);
            setArbejdsfunktion(arbejdsfunktion);
            setRettighed(rettighed);
            setLinkedSystem(linkedSystem);
            setGyldigFra(gyldigFra);
            setGyldigTil(gyldigTil);
            setGodkendelsesdato(null);
        }};
        final Bemyndigelse existingBemyndigelse = new Bemyndigelse() {{
            setKode("Existing");
        }};

        when(bemyndigelseDao.findByKoder(singletonList(kode))).thenReturn(singletonList(bemyndigelse));
        when(bemyndigelseDao.findByInPeriod(bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktion, rettighed, linkedSystem, gyldigFra, gyldigTil)).thenReturn(singletonList(existingBemyndigelse));
        when(systemService.getDateTime()).thenReturn(now);

        manager.godkendBemyndigelser(singletonList(kode));

        verify(bemyndigelseDao).save(argThat(new TypeSafeMatcher<Bemyndigelse>() {
            @Override
            public boolean matchesSafely(Bemyndigelse item) {
                return item.getKode().equals("Existing") && item.getGyldigTil() == gyldigFra;
            }

            @Override
            public void describeTo(Description description) {
            }
        }));
        verify(bemyndigelseDao).save(argThat(new TypeSafeMatcher<Bemyndigelse>() {
            @Override
            public boolean matchesSafely(Bemyndigelse item) {
                return item.getKode().equals(kode) && item.getGodkendelsesdato() == now;
            }

            @Override
            public void describeTo(Description description) {
            }
        }));
    }

    @Test
    public void canCreateApprovedBemyndigelser() throws Exception {
        when(arbejdsfunktionDao.findByKode(arbejdsfunktionKode)).thenReturn(Arbejdsfunktion.createForTest(arbejdsfunktionKode));
        when(rettighedDao.findByKode(rettighedKode)).thenReturn(Rettighed.createForTest(rettighedKode));
        when(linkedSystemDao.findByKode(systemKode)).thenReturn(LinkedSystem.createForTest(systemKode));
        when(systemService.getDateTime()).thenReturn(now);
        when(systemService.createUUIDString()).thenReturn(kode);

        Bemyndigelse bemyndigelse = manager.opretGodkendtBemyndigelse(bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, rettighedKode, systemKode, null, null);

        assertEquals(now, bemyndigelse.getGodkendelsesdato());
        verifyAllFields(bemyndigelse);
        verify(bemyndigelseDao).save(bemyndigelse);
    }

    @Test
    public void willShutdownExistingBemyndigelserWhenCreatingApprovedBemyndigelse() throws Exception {
        final DateTime gyldigFra = now;
        DateTime gyldigTil = now.plusYears(100);
        Bemyndigelse existingBemyndigelse = new Bemyndigelse() {{
            setKode("Existing");
        }};

        when(arbejdsfunktionDao.findByKode(arbejdsfunktionKode)).thenReturn(arbejdsfunktion);
        when(rettighedDao.findByKode(rettighedKode)).thenReturn(rettighed);
        when(linkedSystemDao.findByKode(systemKode)).thenReturn(linkedSystem);
        when(systemService.getDateTime()).thenReturn(now);
        when(systemService.createUUIDString()).thenReturn(kode);
        when(bemyndigelseDao.findByInPeriod(bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktion, rettighed, linkedSystem, gyldigFra, gyldigTil)).thenReturn(singletonList(existingBemyndigelse));


        Bemyndigelse bemyndigelse = manager.opretGodkendtBemyndigelse(bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, rettighedKode, systemKode, null, null);

        verify(bemyndigelseDao).findByInPeriod(bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktion, rettighed, linkedSystem, gyldigFra, gyldigTil);
        verify(bemyndigelseDao).save(argThat(new TypeSafeMatcher<Bemyndigelse>() {
            @Override
            public boolean matchesSafely(Bemyndigelse item) {
                return item.getKode().equals("Existing") && item.getGyldigTil() == gyldigFra;
            }

            @Override
            public void describeTo(Description description) {
            }
        }));
        verify(bemyndigelseDao).save(bemyndigelse);

        assertEquals(now, bemyndigelse.getGodkendelsesdato());
    }

    private void verifyAllFields(Bemyndigelse bemyndigelse) {
        assertEquals(kode, bemyndigelse.getKode());
        assertEquals(now, bemyndigelse.getGyldigFra());
        assertEquals(now.plusYears(100), bemyndigelse.getGyldigTil());
        assertEquals(bemyndigendeCpr, bemyndigelse.getBemyndigendeCpr());
        assertEquals(bemyndigedeCpr, bemyndigelse.getBemyndigedeCpr());
        assertEquals(bemyndigedeCvr, bemyndigelse.getBemyndigedeCvr());
        assertEquals(arbejdsfunktionKode, bemyndigelse.getArbejdsfunktion().getKode());
        //TODO: Hvad med status?
        assertEquals(rettighedKode, bemyndigelse.getRettighed().getKode());
        assertEquals(systemKode, bemyndigelse.getLinkedSystem().getKode());
    }
}
