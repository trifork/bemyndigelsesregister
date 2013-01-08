package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import com.trifork.dgws.CareProviderIdType;
import com.trifork.dgws.DgwsRequestContext;
import com.trifork.dgws.IdCardData;
import com.trifork.dgws.IdCardSystemLog;
import com.trifork.dgws.IdCardType;
import com.trifork.dgws.IdCardUserLog;
import com.trifork.dgws.WhitelistChecker;

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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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
    @Mock DomaeneDao domaeneDao;
    @Mock LinkedSystemDao linkedSystemDao;
    @Mock ArbejdsfunktionDao arbejdsfunktionDao;
    @Mock RettighedDao rettighedDao;
    @Mock DelegerbarRettighedDao delegerbarRettighedDao;
    @Mock SystemService systemService;
    @Mock DgwsRequestContext dgwsRequestContext;
    @Mock ServiceTypeMapper typeMapper;
    @Mock WhitelistChecker whitelistChecker;

    @Mock SoapHeader soapHeader;

    private final DateTime now = new DateTime();

    final String kodeText = "UUID kode";
    final String bemyndigendeCprText = "BemyndigendeCpr";
    final String bemyndigedeCprText = "BemyndigedeCpr";
    final String bemyndigedeCvrText = "BemyndigedeCvr";
    final String arbejdsfunktionKode = "Arbejdsfunktion";
    final String rettighedKode = "Rettighedskode";
    final String domaeneKode = "DomaeneKode";
    final String systemKode = "SystemKode";
    final Status status = Status.GODKENDT;

    void setupDgwsRequestContextForSystem(String cvr) {
    	when(dgwsRequestContext.getIdCardData()).thenReturn(new IdCardData(IdCardType.SYSTEM, 3));
    	when(dgwsRequestContext.getIdCardSystemLog()).thenReturn(new IdCardSystemLog(null, CareProviderIdType.CVR_NUMBER, cvr, null));
    }
    
    void setupDgwsRequestContextForUser(String cpr) {
    	when(dgwsRequestContext.getIdCardData()).thenReturn(new IdCardData(IdCardType.USER, 4));
    	when(dgwsRequestContext.getIdCardUserLog()).thenReturn(new IdCardUserLog(cpr, null,null, null, null,null, null));
    }
    
    @Test
    public void canCreateBemyndigelseAndmodning() throws Exception {
        final Bemyndigelse bemyndigelse = createBemyndigelse(kodeText, null);

        when(bemyndigelseManager.opretAnmodningOmBemyndigelse(
                bemyndigendeCprText, bemyndigedeCprText, bemyndigedeCvrText, arbejdsfunktionKode, rettighedKode, systemKode,
                null, null)).thenReturn(bemyndigelse);
        setupDgwsRequestContextForUser("BemyndigedeCpr");

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
        setupDgwsRequestContextForUser("Evil CPR");

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
        setupDgwsRequestContextForUser(bemyndigendeCprText);

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
        setupDgwsRequestContextForUser("Evil CPR");

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
        setupDgwsRequestContextForUser(bemyndigendeCprText);

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
        setupDgwsRequestContextForUser("Evil CPR");

        service.opretGodkendtBemyndigelse(request, soapHeader);
    }

    @Test
    public void canGetBemyndigelserByBemyndigende() throws Exception {
        final Bemyndigelse bemyndigelse = createBemyndigelse("Bem1", now.minusDays(7));
        setupDgwsRequestContextForUser("Bemyndigende");
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
        setupDgwsRequestContextForUser(bemyndigelse.getBemyndigendeCpr());

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
        system.setKode(this.systemKode);
        bemyndigelse.setLinkedSystem(system);

        final Arbejdsfunktion arbejdsfunktion = new Arbejdsfunktion();
        arbejdsfunktion.setKode(this.arbejdsfunktionKode);
        bemyndigelse.setArbejdsfunktion(arbejdsfunktion);

        final Rettighed rettighed = new Rettighed();
        rettighed.setKode(this.rettighedKode);
        bemyndigelse.setRettighed(rettighed);

        bemyndigelse.setStatus(status);

        bemyndigelse.setGodkendelsesdato(godkendelsesdato);

        bemyndigelse.setGyldigFra(now.minusDays(1));
        bemyndigelse.setGyldigTil(now.plusDays(1));

        return bemyndigelse;
    }

    @Test
    public void canGetBemyndigelserByBemyndigede() throws Exception {
        final Bemyndigelse bemyndigelse = createBemyndigelse("Bem1", now.minusDays(7));
        setupDgwsRequestContextForUser("Bemyndigede");
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
        setupDgwsRequestContextForUser("IDcard CPR");

        service.hentBemyndigelser(request, soapHeader);
    }

    @Test(expected = IllegalAccessError.class)
    public void canNotHentBemyndigelserWhenIDCardCprIsNotInBemyndigelse() throws Exception {
        final HentBemyndigelserRequest request = new HentBemyndigelserRequest() {{
            setKode(kodeText);
        }};
        when(bemyndigelseDao.findByKode(kodeText)).thenReturn(createBemyndigelse(kodeText, now));
        setupDgwsRequestContextForUser("IDcard CPR");

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

        setupDgwsRequestContextForUser("Cpr 1");
        when(bemyndigelseDao.findByKoder(singletonList("TestKode1"))).thenReturn(singletonList(bemyndigelse));
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

        setupDgwsRequestContextForUser("Evil Cpr");
        when(bemyndigelseDao.findByKoder(singletonList("TestKode1"))).thenReturn(singletonList(bemyndigelse));
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

        setupDgwsRequestContextForUser("Cpr 1");
        when(bemyndigelseDao.findByKoder(singletonList("TestKode1"))).thenReturn(singletonList(bemyndigelse));
        when(systemService.getDateTime()).thenReturn(now);

        SletBemyndigelserRequest request = new SletBemyndigelserRequest();
        request.getKode().add(("TestKode1"));
        SletBemyndigelserResponse response = service.sletBemyndigelser(request, soapHeader);

        assertEquals(0, response.getKode().size());
        assertEquals(now.minusDays(1), bemyndigelse.getGyldigTil());
        verify(bemyndigelseDao, never()).save(bemyndigelse);
    }

    @Test
    public void canGetMetadata() throws Exception {
        final String domaeneKode = "Domaene";
        final String systemKode = "System";
        final HentMetadataRequest request = new HentMetadataRequest() {{
            setDomaene(domaeneKode);
            setSystem(systemKode);
        }};

        final Domaene domaene = new Domaene();
        final LinkedSystem linkedSystem = new LinkedSystem();
        final Arbejdsfunktion arbejdsfunktion = new Arbejdsfunktion();
        final Rettighed rettighed = new Rettighed();
        final DelegerbarRettighed delegerbarRettighed = new DelegerbarRettighed();

        when(domaeneDao.findByKode(domaeneKode)).thenReturn(domaene);
        when(linkedSystemDao.findByKode(systemKode)).thenReturn(linkedSystem);

        final List<Arbejdsfunktion> arbejdsfunktionList = asList(arbejdsfunktion);
        final Arbejdsfunktioner jaxbArbejdsfunktioner = new Arbejdsfunktioner();
        final List<Rettighed> rettighedList = asList(rettighed);
        final Rettigheder jaxbRettigheder = new Rettigheder();
        final List<DelegerbarRettighed> delegerbarRettighedList = asList(delegerbarRettighed);
        final DelegerbarRettigheder jaxbDelegerbarRettigheder = new DelegerbarRettigheder();

        when(arbejdsfunktionDao.findBy(domaene, linkedSystem)).thenReturn(arbejdsfunktionList);
        when(rettighedDao.findBy(domaene, linkedSystem)).thenReturn(rettighedList);
        when(delegerbarRettighedDao.findBy(domaene, linkedSystem)).thenReturn(delegerbarRettighedList);
        when(typeMapper.toJaxbArbejdsfunktioner(arbejdsfunktionList)).thenReturn(jaxbArbejdsfunktioner);
        when(typeMapper.toJaxbRettigheder(rettighedList)).thenReturn(jaxbRettigheder);
        when(typeMapper.toJaxbDelegerbarRettigheder(delegerbarRettighedList)).thenReturn(jaxbDelegerbarRettigheder);

        final HentMetadataResponse metadata = service.hentMetadata(request, soapHeader);

        assertEquals(jaxbArbejdsfunktioner, metadata.getArbejdsfunktioner());
        assertEquals(jaxbRettigheder, metadata.getRettigheder());
        assertEquals(jaxbDelegerbarRettigheder, metadata.getDelegerbarRettigheder());
    }

    @Test
    public void canIndlaeseArbejdsfunktioner() throws Exception {
        final IndlaesMetadataRequest request = new IndlaesMetadataRequest() {{
            this.setArbejdsfunktioner(new Arbejdsfunktioner() {{
                getArbejdsfunktion().add(new Arbejdsfunktion() {{
                    this.setArbejdsfunktion("Arbejdsfunktion");
                    this.setBeskrivelse("Beskrivelse");
                    this.setDomaene(domaeneKode);
                    this.setSystem(systemKode);
                }});
            }});
        }};

        final Domaene domaene = new Domaene();
        final LinkedSystem linkedSystem = new LinkedSystem();
        when(domaeneDao.findByKode(domaeneKode)).thenReturn(domaene);
        when(linkedSystemDao.findByKode(systemKode)).thenReturn(linkedSystem);

        assertNotNull(service.indlaesMetadata(request, soapHeader));

        verify(arbejdsfunktionDao).save(argThat(new TypeSafeMatcher<Arbejdsfunktion>() {
            @Override
            public boolean matchesSafely(Arbejdsfunktion item) {
                return allTrue(
                        item.getKode().equals("Arbejdsfunktion"),
                        item.getBeskrivelse().equals("Beskrivelse"),
                        item.getDomaene() == domaene,
                        item.getLinkedSystem() == linkedSystem
                );
            }

            @Override
            public void describeTo(Description description) {
            }
        }));
    }
    

    @Test
    public void canIndlaeseRettigheder() throws Exception {
        final IndlaesMetadataRequest request = new IndlaesMetadataRequest() {{
            this.setRettigheder(new Rettigheder() {{
                getRettighed().add(new Rettighed() {{
                    this.setBeskrivelse("Beskrivelse");
                    this.setDomaene(domaeneKode);
                    this.setSystem(systemKode);
                    this.setRettighed("Rettighed");
                }});
            }});
        }};

        final Domaene domaene = new Domaene();
        final LinkedSystem linkedSystem = new LinkedSystem();
        when(domaeneDao.findByKode(domaeneKode)).thenReturn(domaene);
        when(linkedSystemDao.findByKode(systemKode)).thenReturn(linkedSystem);

        assertNotNull(service.indlaesMetadata(request, soapHeader));

        verify(rettighedDao).save(argThat(new TypeSafeMatcher<Rettighed>() {
            @Override
            public boolean matchesSafely(Rettighed item) {
                return allTrue(
                        item.getBeskrivelse().equals("Beskrivelse"),
                        item.getDomaene() == domaene,
                        item.getLinkedSystem() == linkedSystem,
                        item.getKode().equals("Rettighed")
                );
            }

            @Override
            public void describeTo(Description description) {
            }
        }));
    }

    @Test
    public void canIndlaeseDelegeredeRettigheder() throws Exception {
        final IndlaesMetadataRequest request = new IndlaesMetadataRequest() {{
            this.setDelegerbarRettigheder(new DelegerbarRettigheder() {{
                getDelegerbarRettighed().add(new DelegerbarRettighed() {{
                    this.setArbejdsfunktion("Arbejdsfunktion");
                    this.setDomaene(domaeneKode);
                    this.setRettighed("Rettighed");
                    this.setSystem(systemKode);
                }});
            }});
        }};

        final Domaene domaene = new Domaene();
        final LinkedSystem linkedSystem = new LinkedSystem();
        final Arbejdsfunktion arbejdsfunktion = new Arbejdsfunktion();
        when(domaeneDao.findByKode(domaeneKode)).thenReturn(domaene);
        when(linkedSystemDao.findByKode(systemKode)).thenReturn(linkedSystem);
        when(arbejdsfunktionDao.findByKode("Arbejdsfunktion")).thenReturn(arbejdsfunktion);

        assertNotNull(service.indlaesMetadata(request, soapHeader));

        verify(delegerbarRettighedDao).save(argThat(new TypeSafeMatcher<DelegerbarRettighed>() {
            @Override
            public boolean matchesSafely(DelegerbarRettighed item) {
                return allTrue(
                        item.getArbejdsfunktion() == arbejdsfunktion,
                        item.getDomaene() == domaene,
                        item.getKode().equals("Rettighed"),
                        item.getLinkedSystem() == linkedSystem
                );
            }

            @Override
            public void describeTo(Description description) {
            }
        }));
    }

    @Test
    public void willAuthorizeWhitelistedSystemIdCard() {
    	setupDgwsRequestContextForSystem("12345678");
    	when(whitelistChecker.isSystemWhitelisted("hentBemyndigelser", "12345678")).thenReturn(true);
    	service.authorizeOperationForCpr("hentBemyndigelser", "error message");
    }
    
    @Test(expected = IllegalAccessError.class)
    public void willDenyNonWhitelistedSystemIdCard() {
    	setupDgwsRequestContextForSystem("12345678");
    	when(whitelistChecker.isSystemWhitelisted("hentBemyndigelser", "12345678")).thenReturn(false);
    	service.authorizeOperationForCpr("hentBemyndigelser", "error message");
    }
    
    @Test
    public void willDenyUserIdCardWhitelistedAsSystem() {
    	setupDgwsRequestContextForUser("12345678");
    	when(dgwsRequestContext.getIdCardSystemLog()).thenReturn(new IdCardSystemLog(null, CareProviderIdType.CVR_NUMBER, "12345678", null));
    	when(whitelistChecker.isUserWhitelisted("hentBemyndigelser", "12345678", "1122334455")).thenReturn(false);
    	try {
    		service.authorizeOperationForCpr("hentBemyndigelser", "error message");
    		fail();
    	}
    	catch (IllegalAccessError e) {
    		// expected
    	}
    	verify(whitelistChecker, never()).isSystemWhitelisted(any(String.class), any(String.class));
    }
    
    @Test
    public void willAllowEmptyIndlaesMetadata() throws Exception {
        assertNotNull(service.indlaesMetadata(new IndlaesMetadataRequest(), soapHeader));

        verifyZeroInteractions(arbejdsfunktionDao);
        verifyZeroInteractions(rettighedDao);
        verifyZeroInteractions(delegerbarRettighedDao);
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
