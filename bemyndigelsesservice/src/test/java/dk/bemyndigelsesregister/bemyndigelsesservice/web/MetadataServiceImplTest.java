package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.trifork.dgws.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Status;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ServiceTypeMapper;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ws.soap.SoapHeader;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MetadataServiceImplTest {
    @InjectMocks
    BemyndigelsesServiceImpl service = new BemyndigelsesServiceImpl();

    @Mock
    SystemService systemService;
    @Mock
    DgwsRequestContext dgwsRequestContext;
    @Mock
    ServiceTypeMapper typeMapper;
    @Mock
    WhitelistChecker whitelistChecker;

    @Mock
    SoapHeader soapHeader;

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
        when(dgwsRequestContext.getIdCardUserLog()).thenReturn(new IdCardUserLog(cpr, null, null, null, null, null, null));
    }

    @Test
    public void canGetMetadata() throws Exception {
/* TODO OBJ: Implementer disse funktioner med BEM2 Metadata services
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

        when(arbejdsfunktionDao.findBy(linkedSystem)).thenReturn(arbejdsfunktionList);
        when(rettighedDao.findBy(linkedSystem)).thenReturn(rettighedList);
        when(delegerbarRettighedDao.findBy(linkedSystem)).thenReturn(delegerbarRettighedList);
        when(typeMapper.toJaxbArbejdsfunktioner(arbejdsfunktionList)).thenReturn(jaxbArbejdsfunktioner);
        when(typeMapper.toJaxbRettigheder(rettighedList)).thenReturn(jaxbRettigheder);
        when(typeMapper.toJaxbDelegerbarRettigheder(delegerbarRettighedList)).thenReturn(jaxbDelegerbarRettigheder);

        final HentMetadataResponse metadata = service.hentMetadata(request, soapHeader);

        assertEquals(jaxbArbejdsfunktioner, metadata.getArbejdsfunktioner());
        assertEquals(jaxbRettigheder, metadata.getRettigheder());
        assertEquals(jaxbDelegerbarRettigheder, metadata.getDelegerbarRettigheder());
*/
    }
/*
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
        when(arbejdsfunktionDao.findByKode(linkedSystem, "Arbejdsfunktion")).thenReturn(arbejdsfunktion);

        assertNotNull(service.indlaesMetadata(request, soapHeader));

        verify(delegerbarRettighedDao).save(argThat(new TypeSafeMatcher<DelegerbarRettighed>() {
            @Override
            public boolean matchesSafely(DelegerbarRettighed item) {
                return allTrue(
                        item.getArbejdsfunktion() == arbejdsfunktion
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
    */
}
