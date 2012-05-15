package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import com.trifork.dgws.DgwsRequestContext;
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
    @Mock SystemService systemService;
    @Mock SecurityHelper securityHelper;
    @Mock DgwsRequestContext dgwsRequestContext;

    @Mock SoapHeader soapHeader;

    private final DateTime now = new DateTime();

    final String kodeText = "UUID kode";
    final String bemyndigendeCprText = "BemyndigendeCpr";
    final String bemyndigedeCprText = "BemyndigedeCpr";
    final String bemyndigedeCvrText = "BemyndigedeCvr";
    final String arbejdsfunktionKode = "Arbejdsfunktion";
    final String rettighedKode = "Rettighedskode";
    final String systemKode = "SystemKode";
    final String statusKode = "StatusKode";

    @Test
    public void canCreateBemyndigelseAndmodning() throws Exception {
        final Bemyndigelse bemyndigelse = createBemyndigelse(kodeText, null);

        when(bemyndigelseManager.opretAnmodningOmBemyndigelse(
                bemyndigendeCprText, bemyndigedeCprText, bemyndigedeCvrText, arbejdsfunktionKode, rettighedKode, systemKode,
                null, null)).thenReturn(bemyndigelse);
        when(dgwsRequestContext.getIdCardCpr()).thenReturn("BemyndigedeCpr");

        OpretAnmodningOmBemyndigelserRequest request = new OpretAnmodningOmBemyndigelserRequest() {{
            getAnmodning().add(new Anmodning() {{
                setBemyndigendeCpr("BemyndigendeCpr");
                setBemyndigedeCpr("BemyndigedeCpr");
                setBemyndigedeCvr("BemyndigedeCvr");
                setArbejdsfunktion("Arbejdsfunktion");
                setRettighed("Rettighedskode");
                setSystem("SystemKode");
            }});
        }};

        final OpretAnmodningOmBemyndigelserResponse response = service.opretAnmodningOmBemyndigelser(request, soapHeader);

        verify(bemyndigelseManager).opretAnmodningOmBemyndigelse(bemyndigendeCprText, bemyndigedeCprText, bemyndigedeCvrText, arbejdsfunktionKode, rettighedKode, systemKode, null, null);

        assertEquals(1, response.getBemyndigelse().size());
        final dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse responseBemyndigelse = response.getBemyndigelse().get(0);
        assertEquals(kodeText, responseBemyndigelse.getKode());
        assertEquals(bemyndigendeCprText, responseBemyndigelse.getBemyndigendeCpr());
        assertEquals(bemyndigedeCprText, responseBemyndigelse.getBemyndigedeCpr());
        assertEquals(arbejdsfunktionKode, responseBemyndigelse.getArbejdsfunktion());
        assertEquals(rettighedKode, responseBemyndigelse.getRettighed());
        assertEquals(systemKode, responseBemyndigelse.getSystem());
    }

    @Test(expected = IllegalAccessError.class)
    public void canNotCreateBemyndigelseAndmodningForAnotherCpr() throws Exception {
        final Bemyndigelse bemyndigelse = createBemyndigelse(kodeText, null);

        when(bemyndigelseManager.opretAnmodningOmBemyndigelse(
                bemyndigendeCprText, bemyndigedeCprText, bemyndigedeCvrText, arbejdsfunktionKode, rettighedKode, systemKode,
                null, null)).thenReturn(bemyndigelse);
        when(dgwsRequestContext.getIdCardCpr()).thenReturn("Evil CPR");

        OpretAnmodningOmBemyndigelserRequest request = new OpretAnmodningOmBemyndigelserRequest() {{
            getAnmodning().add(new Anmodning() {{
                setBemyndigendeCpr("BemyndigendeCpr");
                setBemyndigedeCpr("BemyndigedeCpr");
                setBemyndigedeCvr("BemyndigedeCvr");
                setArbejdsfunktion("Arbejdsfunktion");
                setRettighed("Rettighedskode");
                setSystem("SystemKode");
            }});
        }};

        service.opretAnmodningOmBemyndigelser(request, soapHeader);
    }

    @Test
    public void canApproveBemyndigelse() throws Exception {
        final GodkendBemyndigelseRequest request = new GodkendBemyndigelseRequest() {{
            getBemyndigelsesKode().add(kodeText);
        }};
        final Bemyndigelse bemyndigelse = createBemyndigelse(kodeText, null);

        when(bemyndigelseManager.godkendBemyndigelser(singletonList(kodeText))).thenReturn(singletonList(bemyndigelse));
        when(dgwsRequestContext.getIdCardCpr()).thenReturn(bemyndigendeCprText);

        final GodkendBemyndigelseResponse response = service.godkendBemyndigelse(request, soapHeader);

        assertEquals(1, response.getBemyndigelser().size());
        assertEquals(kodeText, response.getBemyndigelser().get(0).getKode());
    }

    @Test(expected = IllegalAccessError.class)
    public void canNotApproveBemyndigelseWithAnotherCpr() throws Exception {
        final GodkendBemyndigelseRequest request = new GodkendBemyndigelseRequest() {{
            getBemyndigelsesKode().add(kodeText);
        }};
        final Bemyndigelse bemyndigelse = createBemyndigelse(kodeText, null);

        when(bemyndigelseManager.godkendBemyndigelser(singletonList(kodeText))).thenReturn(singletonList(bemyndigelse));
        when(dgwsRequestContext.getIdCardCpr()).thenReturn("Evil CPR");

        service.godkendBemyndigelse(request, soapHeader);
    }

    @Test
    public void canCreateApprovedBemyndigelse() throws Exception {
        final OpretGodkendteBemyndigelserRequest request = new OpretGodkendteBemyndigelserRequest() {{
            getBemyndigelse().add(new Bemyndigelse() {{
                setBemyndigendeCpr(bemyndigendeCprText);
                setBemyndigedeCpr(bemyndigedeCprText);
                setBemyndigedeCvr(bemyndigedeCvrText);
                setSystem(systemKode);
                setArbejdsfunktion(arbejdsfunktionKode);
                setRettighed(rettighedKode);
                setGyldigFra(new XMLGregorianCalendarImpl(now.toGregorianCalendar()));
            }});
        }};

        final Bemyndigelse bemyndigelse = createBemyndigelse(kodeText, now);

        when(bemyndigelseManager.opretGodkendtBemyndigelse(eq(bemyndigendeCprText), eq(bemyndigedeCprText), eq(bemyndigedeCvrText), eq(arbejdsfunktionKode), eq(rettighedKode), eq(systemKode), any(DateTime.class), isNull(DateTime.class))).thenReturn(bemyndigelse);
        when(dgwsRequestContext.getIdCardCpr()).thenReturn(bemyndigendeCprText);

        final OpretGodkendteBemyndigelserResponse response = service.opretGodkendtBemyndigelse(request, soapHeader);

        assertEquals(1, response.getBemyndigelse().size());
        assertEquals(kodeText, response.getBemyndigelse().get(0).getKode());
    }

    @Test(expected = IllegalAccessError.class)
    public void canNotCreateApprovedBemyndigelseForAnotherCpr() throws Exception {
        final OpretGodkendteBemyndigelserRequest request = new OpretGodkendteBemyndigelserRequest() {{
            getBemyndigelse().add(new Bemyndigelse() {{
                setBemyndigendeCpr(bemyndigendeCprText);
                setBemyndigedeCpr(bemyndigedeCprText);
                setBemyndigedeCvr(bemyndigedeCvrText);
                setSystem(systemKode);
                setArbejdsfunktion(arbejdsfunktionKode);
                setRettighed(rettighedKode);
                setGyldigFra(new XMLGregorianCalendarImpl(now.toGregorianCalendar()));
            }});
        }};
        final Bemyndigelse bemyndigelse = createBemyndigelse(kodeText, now);

        when(bemyndigelseManager.opretGodkendtBemyndigelse(eq(bemyndigendeCprText), eq(bemyndigedeCprText), eq(bemyndigedeCvrText), eq(arbejdsfunktionKode), eq(rettighedKode), eq(systemKode), any(DateTime.class), isNull(DateTime.class))).thenReturn(bemyndigelse);
        when(dgwsRequestContext.getIdCardCpr()).thenReturn("Evil CPR");

        service.opretGodkendtBemyndigelse(request, soapHeader);
    }

    @Test
    public void canGetBemyndigelserByBemyndigende() throws Exception {
        final Bemyndigelse bemyndigelse = createBemyndigelse("Bem1", now.minusDays(7));
        when(dgwsRequestContext.getIdCardCpr()).thenReturn("Bemyndigende");
        when(bemyndigelseDao.findByBemyndigendeCpr("Bemyndigende")).thenReturn(asList(bemyndigelse));

        final HentBemyndigelserRequest request = new HentBemyndigelserRequest() {{
            setBemyndigendeCpr("Bemyndigende");
        }};
        HentBemyndigelserResponse response = service.hentBemyndigelser(request, soapHeader);

        assertThat(response.getBemyndigelse(), hasItem(new TypeSafeMatcher<dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse>() {
            @Override
            public boolean matchesSafely(dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse item) {
                return item.getKode().equals("Bem1");
            }

            @Override
            public void describeTo(Description description) { }
        }));
    }

    @Test
    public void canGetBemyndigelserByKode() throws Exception {
        final Bemyndigelse bemyndigelse = createBemyndigelse(kodeText, now.minusDays(7));
        when(bemyndigelseDao.findByKode(kodeText)).thenReturn(bemyndigelse);
        when(dgwsRequestContext.getIdCardCpr()).thenReturn(bemyndigelse.getBemyndigendeCpr());

        final HentBemyndigelserRequest request = new HentBemyndigelserRequest() {{
            setKode(kodeText);
        }};
        HentBemyndigelserResponse response = service.hentBemyndigelser(request, soapHeader);

        assertThat(response.getBemyndigelse(), hasItem(new TypeSafeMatcher<dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse>() {
            @Override
            public boolean matchesSafely(dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse item) {
                return item.getKode().equals(kodeText);
            }

            @Override
            public void describeTo(Description description) { }
        }));
    }

    private Bemyndigelse createBemyndigelse(final String kode, DateTime godkendelsesdato) {
        final Bemyndigelse bemyndigelse = new Bemyndigelse();

        bemyndigelse.setKode(kode);

        bemyndigelse.setBemyndigendeCpr(bemyndigendeCprText);
        bemyndigelse.setBemyndigedeCpr(bemyndigedeCprText);
        bemyndigelse.setBemyndigedeCvr(bemyndigedeCvrText);

        final LinkedSystem system = new LinkedSystem();
        system.setSystem(this.systemKode);
        bemyndigelse.setLinkedSystem(system);

        final Arbejdsfunktion arbejdsfunktion = new Arbejdsfunktion();
        arbejdsfunktion.setArbejdsfunktion(this.arbejdsfunktionKode);
        bemyndigelse.setArbejdsfunktion(arbejdsfunktion);

        final Rettighed rettighed = new Rettighed();
        rettighed.setRettighedskode(this.rettighedKode);
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
        when(dgwsRequestContext.getIdCardCpr()).thenReturn("Bemyndigede");
        when(bemyndigelseDao.findByBemyndigedeCpr("Bemyndigede")).thenReturn(asList(bemyndigelse));

        final HentBemyndigelserRequest request = new HentBemyndigelserRequest() {{
            setBemyndigedeCpr("Bemyndigede");
        }};
        HentBemyndigelserResponse response = service.hentBemyndigelser(request, soapHeader);

        assertThat(response.getBemyndigelse(), hasItem(new TypeSafeMatcher<dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse>() {
                    @Override
                    public boolean matchesSafely(dk.nsi.bemyndigelse._2012._05._01.Bemyndigelse item) {
                        return item.getKode().equals("Bem1");
                    }

                    @Override
                    public void describeTo(Description description) { }
                }
        ));
    }

    @Test(expected = IllegalAccessError.class)
    public void canNotHentBemyndigelserWhenBemyndigeCprIsDifferentFromIDCard() throws Exception {
        final HentBemyndigelserRequest request = new HentBemyndigelserRequest() {{
            setBemyndigendeCpr("Wrong CPR");
        }};
        when(dgwsRequestContext.getIdCardCpr()).thenReturn("IDcard CPR");

        service.hentBemyndigelser(request, soapHeader);
    }

    @Test(expected = IllegalAccessError.class)
    public void canNotHentBemyndigelserWhenIDCardCprIsNotInBemyndigelse() throws Exception {
        final HentBemyndigelserRequest request = new HentBemyndigelserRequest() {{
            setKode(kodeText);
        }};
        when(bemyndigelseDao.findByKode(kodeText)).thenReturn(createBemyndigelse(kodeText, now));
        when(dgwsRequestContext.getIdCardCpr()).thenReturn("IDcard CPR");

        service.hentBemyndigelser(request, soapHeader);
    }

    @Test
    public void canDeleteBemyndigelse() throws Exception {
        Bemyndigelse bemyndigelse = new Bemyndigelse() {{
            setId(1l);
            setKode("TestKode1");
            setGyldigTil(now.plusYears(1));
            setBemyndigendeCpr("Cpr 1");
        }};

        when(dgwsRequestContext.getIdCardCpr()).thenReturn("Cpr 1");
        when(bemyndigelseDao.findByKode("TestKode1")).thenReturn(bemyndigelse);
        when(systemService.getDateTime()).thenReturn(now);

        SletBemyndigelserRequest request = new SletBemyndigelserRequest();
        request.getKode().add("TestKode1");
        SletBemyndigelserResponse response = service.sletBemyndigelser(request, soapHeader);

        assertEquals("TestKode1", response.getKode().get(0));
        assertEquals(now, bemyndigelse.getGyldigTil());
        verify(bemyndigelseDao).save(bemyndigelse);
    }

    @Test(expected = IllegalAccessError.class)
    public void canNotDeleteBemyndigelseWithAnotherCpr() throws Exception {
        Bemyndigelse bemyndigelse = new Bemyndigelse() {{
            setId(1l);
            setKode("TestKode1");
            setGyldigTil(now.plusYears(1));
            setBemyndigendeCpr("Cpr 1");
        }};

        when(dgwsRequestContext.getIdCardCpr()).thenReturn("Evil Cpr");
        when(bemyndigelseDao.findByKode("TestKode1")).thenReturn(bemyndigelse);
        when(systemService.getDateTime()).thenReturn(now);

        SletBemyndigelserRequest request = new SletBemyndigelserRequest() {{
            getKode().add("TestKode1");
        }};
        service.sletBemyndigelser(request, soapHeader);
    }

    @Test
    public void willNotDeleteBemyndigelseWithPastGyldigTil() throws Exception {
        Bemyndigelse bemyndigelse = new Bemyndigelse() {{
            setId(1l);
            setKode("TestKode1");
            setGyldigTil(now.minusDays(1));
            setBemyndigendeCpr("Cpr 1");
        }};

        when(dgwsRequestContext.getIdCardCpr()).thenReturn("Cpr 1");
        when(bemyndigelseDao.findByKode("TestKode1")).thenReturn(bemyndigelse);
        when(systemService.getDateTime()).thenReturn(now);

        SletBemyndigelserRequest request = new SletBemyndigelserRequest();
        request.getKode().add(("TestKode1"));
        SletBemyndigelserResponse response = service.sletBemyndigelser(request, soapHeader);

        assertEquals(0, response.getKode().size());
        assertEquals(now.minusDays(1), bemyndigelse.getGyldigTil());
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
