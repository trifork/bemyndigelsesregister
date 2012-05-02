package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.trifork.dgws.util.SecurityHelper;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.request.HentBemyndigelserRequest;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.request.OpretAnmodningOmBemyndigelseRequest;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.request.SletBemyndigelserRequest;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.request.GodkendBemyndigelseRequest;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.response.GodkendBemyndigelseResponse;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.response.HentBemyndigelserResponse;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.response.OpretGodkendtBemyndigelseResponse;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.response.SletBemyndigelserResponse;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.springframework.ws.soap.SoapHeader;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.junit.internal.matchers.IsCollectionContaining.*;
import static org.mockito.Mockito.*;

public class BemyndigelsesServiceImplTest {
    BemyndigelsesServiceImpl service = new BemyndigelsesServiceImpl();
    BemyndigelseDao bemyndigelseDao = mock(BemyndigelseDao.class);
    ArbejdsfunktionDao arbejdsfunktionDao = mock(ArbejdsfunktionDao.class);
    StatusTypeDao statusTypeDao = mock(StatusTypeDao.class);
    RettighedDao rettighedDao = mock(RettighedDao.class);
    private final LinkedSystemDao linkedSystemDao = mock(LinkedSystemDao.class);
    SystemService systemService = mock(SystemService.class);
    SecurityHelper securityHelper = mock(SecurityHelper.class);

    SoapHeader soapHeader = mock(SoapHeader.class);
    private final DateTime now = new DateTime();

    @Before
    public void setUp() throws Exception {
        service.bemyndigelseDao = bemyndigelseDao;
        service.systemService = systemService;
        service.securityHelper = securityHelper;
        service.arbejdsfunktionDao = arbejdsfunktionDao;
        service.statusTypeDao = statusTypeDao;
        service.rettighedDao = rettighedDao;
        service.linkedSystemDao = linkedSystemDao;
    }

    @Test
    public void canCreateBemyndigelseAndmodning() throws Exception {
        final String kode = "UUID kode";
        final Arbejdsfunktion arbejdsfunktion = new Arbejdsfunktion();
        final StatusType statusType = new StatusType();
        final Rettighed rettighed = new Rettighed();
        final LinkedSystem linkedSystem = new LinkedSystem();
        final DateTime now = new DateTime();

        when(systemService.createUUIDString()).thenReturn(kode);
        when(arbejdsfunktionDao.findByArbejdsfunktion("Arbejdsfunktion")).thenReturn(arbejdsfunktion);
        when(statusTypeDao.get(1l)).thenReturn(statusType);
        when(rettighedDao.findByRettighedskode("Rettighedskode")).thenReturn(rettighed);
        when(linkedSystemDao.findBySystem("SystemKode")).thenReturn(linkedSystem);
        when(systemService.getDateTime()).thenReturn(now);

        OpretAnmodningOmBemyndigelseRequest request = new OpretAnmodningOmBemyndigelseRequest() {{
            setBemyndigedeCpr("BemyndigedeCpr");
            setBemyndigendeCpr("BemyndigendeCpr");
            setArbejdsfunktion("Arbejdsfunktion");
            setRettighed("Rettighedskode");
            setLinkedSystem("SystemKode");
        }};

        service.opretAnmodningOmBemyndigelser(request, soapHeader);

        verify(bemyndigelseDao).save(argThat(new TypeSafeMatcher<Bemyndigelse>() {
            @Override
            public boolean matchesSafely(Bemyndigelse item) {
                return allTrue(
                        item.getKode().equals(kode),
                        item.getBemyndigendeCpr().equals("BemyndigendeCpr"),
                        item.getArbejdsfunktion() == arbejdsfunktion,
                        item.getStatus() == statusType,
                        item.getRettighed() == rettighed,
                        item.getLinkedSystem() == linkedSystem,
                        item.getGyldigFra() == now,
                        item.getGyldigTil().equals(now.plusYears(99))
                );
            }
            @Override
            public void describeTo(Description description) { }
        }));
    }

    @Test
    public void canApproveBemyndigelse() throws Exception {
        final GodkendBemyndigelseRequest request = new GodkendBemyndigelseRequest() {{
            setBemyndigelsesKode("Kode 1");
        }};
        final Bemyndigelse bemyndigelse = new Bemyndigelse() {{
            setKode("Kode 1");
        }};
        final DateTime now = new DateTime();

        when(bemyndigelseDao.findByKode("Kode 1")).thenReturn(bemyndigelse);
        when(systemService.getDateTime()).thenReturn(now);

        final GodkendBemyndigelseResponse response = service.godkendBemyndigelse(request, soapHeader);

        assertEquals("Kode 1", response.getGodkendtBemyndigelsesKode());
        assertEquals(now, bemyndigelse.getGodkendelsesdato());
        verify(bemyndigelseDao).save(bemyndigelse);
    }

    @Test
    public void canCreateApprovedBemyndigelse() throws Exception {
        final DateTime now = new DateTime();
        final OpretGodkendtBemyndigelseRequest request = new OpretGodkendtBemyndigelseRequest() {{
            setBemyndigende("bemyndigendeCpr");
            setBemyndigede("bemyndigedeCpr");
            setBemyndigedeCvr("bemyndigedeCvr");
            setSystem("system");
            setArbejdsFunktion("arbejdsfunktion");
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

        final OpretGodkendtBemyndigelseResponse response = service.opretGodkendtBemyndigelse(request);

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
        final Bemyndigelse bemyndigelse = new Bemyndigelse() {{
            setKode("Bem1");
        }};
        when(bemyndigelseDao.findByBemyndigendeCpr("Bemyndigende")).thenReturn(asList(bemyndigelse));

        final HentBemyndigelserRequest request = new HentBemyndigelserRequest() {{
            setBemyndigende("Bemyndigende");
        }};
        HentBemyndigelserResponse response = service.hentBemyndigelser(request, soapHeader);

        assertThat(response.getBemyndigelser(), hasItem("Bem1"));
    }

    @Test
    public void canGetBemyndigelserByBemyndigede() throws Exception {
        final Bemyndigelse bemyndigelse = new Bemyndigelse() {{
            setKode("Bem1");
        }};
        when(bemyndigelseDao.findByBemyndigedeCpr("Bemyndigede")).thenReturn(asList(bemyndigelse));

        final HentBemyndigelserRequest request = new HentBemyndigelserRequest() {{
            setBemyndigede("Bemyndigede");
        }};
        HentBemyndigelserResponse response = service.hentBemyndigelser(request, soapHeader);

        assertThat(response.getBemyndigelser(), hasItem("Bem1"));
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
        request.setBemyndigelsesKoder(asList("TestKode1"));
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
        request.setBemyndigelsesKoder(asList("TestKode1"));
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
        request.setBemyndigelsesKoder(asList("TestKode1"));
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
