package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.trifork.dgws.util.SecurityHelper;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.BemyndigelseDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.request.SletBemyndigelserRequest;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.response.SletBemyndigelserResponse;
import dk.bemyndigelsesregister.shared.service.SystemService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ws.soap.SoapHeader;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BemyndigelsesServiceImplTest {
    BemyndigelsesServiceImpl service = new BemyndigelsesServiceImpl();
    BemyndigelseDao bemyndigelseDao = mock(BemyndigelseDao.class);
    SystemService systemService = mock(SystemService.class);
    SecurityHelper securityHelper = mock(SecurityHelper.class);
    SoapHeader soapHeader = mock(SoapHeader.class);

    private final DateTime now = new DateTime();

    @Before
    public void setUp() throws Exception {
        service.bemyndigelseDao = bemyndigelseDao;
        service.systemService = systemService;
        service.securityHelper = securityHelper;
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
}
