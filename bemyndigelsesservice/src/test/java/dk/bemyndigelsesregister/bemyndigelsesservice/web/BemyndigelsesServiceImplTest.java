package dk.bemyndigelsesregister.bemyndigelsesservice.web;

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
    SoapHeader soapHeader = mock(SoapHeader.class);

    @Before
    public void setUp() throws Exception {
        service.bemyndigelseDao = bemyndigelseDao;
        service.systemService = systemService;
    }

    @Test
    public void willDeleteBemyndigelse() throws Exception {
        DateTime now = new DateTime();
        Bemyndigelse bemyndigelse = new Bemyndigelse() {{
            setId(1l);
            setKode("TestKode1");
        }};

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
        final DateTime now = new DateTime();
        Bemyndigelse bemyndigelse = new Bemyndigelse() {{
            setId(1l);
            setKode("TestKode1");
            setGyldigTil(now.minusDays(1));
        }};

        when(bemyndigelseDao.findByKode("TestKode1")).thenReturn(bemyndigelse);
        when(systemService.getDateTime()).thenReturn(now);

        SletBemyndigelserRequest request = new SletBemyndigelserRequest();
        request.setBemyndigelsesKoder(asList("TestKode1"));
        SletBemyndigelserResponse response = service.sletBemyndigelser(request, soapHeader);

        assertEquals(0, response.getSlettedeBemyndigelsesKoder().size());
        assertEquals(now.minusDays(1), bemyndigelse.getGyldigTil());
        verify(bemyndigelseDao, never()).save(bemyndigelse);
    }
}
