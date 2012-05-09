package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.trifork.dgws.util.SecurityHelper;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.BemyndigelseManager;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.bemyndigelse._2012._05._01.*;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ws.soap.SoapHeader;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.junit.internal.matchers.IsCollectionContaining.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BemyndigelsesServiceImplTest {
    @InjectMocks
    BemyndigelsesServiceImpl service = new BemyndigelsesServiceImpl();

    @Mock BemyndigelseManager bemyndigelseManager;
    @Mock BemyndigelseDao bemyndigelseDao;
    @Mock ArbejdsfunktionDao arbejdsfunktionDao;
    @Mock StatusTypeDao statusTypeDao;
    @Mock RettighedDao rettighedDao;
    @Mock LinkedSystemDao linkedSystemDao;
    @Mock SystemService systemService;
    @Mock SecurityHelper securityHelper;

    SoapHeader soapHeader = mock(SoapHeader.class);
    private final DateTime now = new DateTime();

    final String kode = "UUID kode";
    final String bemyndigendeCpr = "BemyndigendeCpr";
    final String bemyndigedeCpr = "BemyndigedeCpr";
    final String bemyndigedeCvr = "BemyndigedeCvr";
    final String arbejdsfunktion = "Arbejdsfunktion";
    final String rettighedskode = "Rettighedskode";
    final String systemKode = "SystemKode";
    final String statusKode = "StatusKode";

    @Test
    public void canCreateBemyndigelseAndmodning() throws Exception {
        final Bemyndigelse bemyndigelse = createBemyndigelse(kode, null);

        when(bemyndigelseManager.opretAnmodningOmBemyndigelse(
                bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktion, rettighedskode, systemKode,
                null, null)).thenReturn(bemyndigelse);

        OpretAnmodningOmBemyndigelseRequest request = new OpretAnmodningOmBemyndigelseRequest() {{
            getAnmodninger().add(new Anmodninger() {{
                setBemyndigendeCpr("BemyndigendeCpr");
                setBemyndigedeCpr("BemyndigedeCpr");
                setBemyndigedeCvr("BemyndigedeCvr");
                setArbejdsfunktion("Arbejdsfunktion");
                setRettighed("Rettighedskode");
                setSystem("SystemKode");
            }});
        }};

        final OpretAnmodningOmBemyndigelseResponse response = service.opretAnmodningOmBemyndigelser(request, soapHeader);

        verify(bemyndigelseManager).opretAnmodningOmBemyndigelse(bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktion, rettighedskode, systemKode, null, null);

        assertEquals(1, response.getBemyndigelser().size());
        final dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse responseBemyndigelse = response.getBemyndigelser().get(0);
        assertEquals(kode, responseBemyndigelse.getKode());
        assertEquals(bemyndigendeCpr, responseBemyndigelse.getBemyndigende());
        assertEquals(bemyndigedeCpr, responseBemyndigelse.getBemyndigede());
        assertEquals(arbejdsfunktion, responseBemyndigelse.getArbejdsfunktion());
        assertEquals(rettighedskode, responseBemyndigelse.getRettighedskode());
        assertEquals(systemKode, responseBemyndigelse.getSystem());
    }

    @Test
    public void canApproveBemyndigelse() throws Exception {
        final GodkendBemyndigelseRequest request = new GodkendBemyndigelseRequest() {{
            getBemyndigelsesKoder().add(kode);
        }};
        final Bemyndigelse bemyndigelse = createBemyndigelse(kode, null);

        when(bemyndigelseManager.godkendBemyndigelser(singletonList(kode))).thenReturn(singletonList(bemyndigelse));

        final GodkendBemyndigelseResponse response = service.godkendBemyndigelse(request, soapHeader);

        assertEquals(1, response.getBemyndigelser().size());
        assertEquals(kode, response.getBemyndigelser().get(0).getKode());
    }

    @Test
    public void canCreateApprovedBemyndigelse() throws Exception {
        final DateTime now = new DateTime();
        final OpretGodkendtBemyndigelseRequest request = new OpretGodkendtBemyndigelseRequest() {{
            setBemyndigende("bemyndigendeCpr");
            setBemyndigede("bemyndigedeCpr");
            setBemyndigedeCVR("bemyndigedeCvr");
            setSystem("system");
            setArbejdsfunktion("arbejdsfunktion");
            setRettighedskode("rettighed");
        }};
        final LinkedSystem system = new LinkedSystem();
        final Arbejdsfunktion arbejdsfunktion = new Arbejdsfunktion();
        final Rettighed rettighed = new Rettighed();

        when(systemService.createUUIDString()).thenReturn("UUID kode");
        when(systemService.getDateTime()).thenReturn(now);
        when(linkedSystemDao.findBySystem("system")).thenReturn(system);
        when(arbejdsfunktionDao.findByArbejdsfunktion("arbejdsfunktion")).thenReturn(arbejdsfunktion);
        when(rettighedDao.findByRettighedskode("rettighed")).thenReturn(rettighed);

        final OpretGodkendtBemyndigelseResponse response = service.opretGodkendtBemyndigelse(request, soapHeader);

        assertEquals("UUID kode", response.getGodkendtBemyndigelsesKode());
        verify(bemyndigelseDao).save(argThat(new TypeSafeMatcher<Bemyndigelse>() {
            @Override
            public boolean matchesSafely(Bemyndigelse item) {
                return allTrue(
                        item.getBemyndigendeCpr().equals("bemyndigendeCpr"),
                        item.getBemyndigedeCpr().equals("bemyndigedeCpr"),
                        item.getBemyndigedeCvr().equals("bemyndigedeCvr"),
                        item.getKode().equals("UUID kode"),
                        item.getGodkendelsesdato() == now,
                        item.getLinkedSystem() == system,
                        item.getArbejdsfunktion() == arbejdsfunktion,
                        item.getRettighed() == rettighed
                        );
            }

            @Override
            public void describeTo(Description description) {
            }
        }));
    }

    @Test
    public void canGetBemyndigelserByBemyndigende() throws Exception {
        final Bemyndigelse bemyndigelse = createBemyndigelse("Bem1", now.minusDays(7));
        when(bemyndigelseDao.findByBemyndigendeCpr("Bemyndigende")).thenReturn(asList(bemyndigelse));

        final HentBemyndigelserRequest request = new HentBemyndigelserRequest() {{
            setBemyndigende("Bemyndigende");
        }};
        HentBemyndigelserResponse response = service.hentBemyndigelser(request, soapHeader);

        assertThat(response.getBemyndigelser(), hasItem(new TypeSafeMatcher<dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse>() {
            @Override
            public boolean matchesSafely(dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse item) {
                return item.getKode().equals("Bem1");
            }

            @Override
            public void describeTo(Description description) { }
        }));
    }

    private Bemyndigelse createBemyndigelse(final String kode, DateTime godkendelsesdato) {
        final Bemyndigelse bemyndigelse = new Bemyndigelse();

        bemyndigelse.setKode(kode);

        bemyndigelse.setBemyndigendeCpr(bemyndigendeCpr);
        bemyndigelse.setBemyndigedeCpr(bemyndigedeCpr);
        bemyndigelse.setBemyndigedeCvr(bemyndigedeCvr);

        final LinkedSystem system = new LinkedSystem();
        system.setSystem(this.systemKode);
        bemyndigelse.setLinkedSystem(system);

        final Arbejdsfunktion arbejdsfunktion = new Arbejdsfunktion();
        arbejdsfunktion.setArbejdsfunktion(this.arbejdsfunktion);
        bemyndigelse.setArbejdsfunktion(arbejdsfunktion);

        final Rettighed rettighed = new Rettighed();
        rettighed.setRettighedskode(this.rettighedskode);
        bemyndigelse.setRettighed(rettighed);

        final StatusType status = new StatusType();
        status.setStatus(this.statusKode);
        bemyndigelse.setStatus(status);

        bemyndigelse.setGodkendelsesdato(godkendelsesdato);

        bemyndigelse.setGyldigFra(now.minusDays(1));
        bemyndigelse.setGyldigTil(now.plusDays(1));

        return bemyndigelse;
    }

    @Test
    public void canGetBemyndigelserByBemyndigede() throws Exception {
        final Bemyndigelse bemyndigelse = createBemyndigelse("Bem1", now.minusDays(7));
        when(bemyndigelseDao.findByBemyndigedeCpr("Bemyndigede")).thenReturn(asList(bemyndigelse));

        final HentBemyndigelserRequest request = new HentBemyndigelserRequest() {{
            setBemyndigede("Bemyndigede");
        }};
        HentBemyndigelserResponse response = service.hentBemyndigelser(request, soapHeader);

        assertThat(response.getBemyndigelser(), hasItem(new TypeSafeMatcher<dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse>() {
                    @Override
                    public boolean matchesSafely(dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse item) {
                        return item.getKode().equals("Bem1");
                    }

                    @Override
                    public void describeTo(Description description) { }
                }
        ));
    }

    @Test
    public void willDeleteBemyndigelse() throws Exception {
        Bemyndigelse bemyndigelse = new Bemyndigelse() {{
            setId(1l);
            setKode("TestKode1");
            setGyldigTil(now.plusYears(1));
            setBemyndigendeCpr("Cpr 1");
        }};

        when(securityHelper.getCpr(soapHeader)).thenReturn("Cpr 1");
        when(bemyndigelseDao.findByKode("TestKode1")).thenReturn(bemyndigelse);
        when(systemService.getDateTime()).thenReturn(now);

        SletBemyndigelserRequest request = new SletBemyndigelserRequest();
        request.getBemyndigelsesKoder().add("TestKode1");
        SletBemyndigelserResponse response = service.sletBemyndigelser(request, soapHeader);

        assertEquals("TestKode1", response.getSlettedeBemyndigelsesKoder().get(0));
        assertEquals(now, bemyndigelse.getGyldigTil());
        verify(bemyndigelseDao).save(bemyndigelse);
    }

    @Test
    public void willNotDeleteBemyndigelseWithPastGyldigTil() throws Exception {
        Bemyndigelse bemyndigelse = new Bemyndigelse() {{
            setId(1l);
            setKode("TestKode1");
            setGyldigTil(now.minusDays(1));
            setBemyndigendeCpr("Cpr 1");
        }};

        when(securityHelper.getCpr(soapHeader)).thenReturn("Cpr 1");
        when(bemyndigelseDao.findByKode("TestKode1")).thenReturn(bemyndigelse);
        when(systemService.getDateTime()).thenReturn(now);

        SletBemyndigelserRequest request = new SletBemyndigelserRequest();
        request.getBemyndigelsesKoder().add(("TestKode1"));
        SletBemyndigelserResponse response = service.sletBemyndigelser(request, soapHeader);

        assertEquals(0, response.getSlettedeBemyndigelsesKoder().size());
        assertEquals(now.minusDays(1), bemyndigelse.getGyldigTil());
        verify(bemyndigelseDao, never()).save(bemyndigelse);
    }

    @Test
    public void willDeclineWhenCprIsDifferentFromBemyndigendeCpr() throws Exception {
        Bemyndigelse bemyndigelse = new Bemyndigelse() {{
            setId(1l);
            setKode("TestKode1");
            setGyldigTil(now.minusDays(1));
            setBemyndigendeCpr("Cpr 1");
        }};

        when(securityHelper.getCpr(soapHeader)).thenReturn("Evil Cpr");
        when(bemyndigelseDao.findByKode("TestKode1")).thenReturn(bemyndigelse);
        when(systemService.getDateTime()).thenReturn(now);

        SletBemyndigelserRequest request = new SletBemyndigelserRequest();
        request.getBemyndigelsesKoder().add("TestKode1");
        SletBemyndigelserResponse response = null;
        try {
            response = service.sletBemyndigelser(request, soapHeader);
            fail("Did not throw exception");
        } catch (IllegalAccessError e) {
            assertEquals("User has different CPR than BemyndigedeCpr for kode=TestKode1", e.getMessage());
        }

        assertNull(response);
        verify(bemyndigelseDao, never()).save(bemyndigelse);
    }

    public boolean allTrue(boolean... eval) {
        int a = 0;
        for (boolean b : eval) {
            if (!b) {
                System.out.println("Arg " + a + " was not true");
                return false;
            }
            a++;
        }
        return true;
    }
}
