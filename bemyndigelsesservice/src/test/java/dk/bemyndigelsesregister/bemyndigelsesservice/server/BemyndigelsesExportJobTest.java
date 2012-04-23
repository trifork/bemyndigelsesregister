package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.BemyndigelseDao;
import generated.BemyndigelserType;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;

import java.math.BigInteger;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

public class BemyndigelsesExportJobTest {
    BemyndigelsesExportJob job = new BemyndigelsesExportJob();
    BemyndigelseDao bemyndigelseDao = mock(BemyndigelseDao.class);
    NspManager nspManager = mock(NspManager.class);

    @Before
    public void setUp() throws Exception {
        job.nspManager = nspManager;
        job.bemyndigelseDao = bemyndigelseDao;
    }

    @Test
    public void willSendBemyndigelserToNsp() throws Exception {
        final List<Bemyndigelse> bemyndigelser = asList(createBemyndigelse("bemyndigede cpr 1"), createBemyndigelse("bemyndigede cpr 2"));
        final DateTime startTime = new DateTime();
        final DateTime lastRun = startTime.minusHours(1);

        when(bemyndigelseDao.findBySidstModificeretGreaterThan(lastRun)).thenReturn(bemyndigelser);

        job.doExport(lastRun, startTime);

        verify(nspManager).send(bemyndigelserEq(bemyndigelser), eq(startTime));
    }

    private Bemyndigelse createBemyndigelse(String bemyndigedeCpr) {
        final Bemyndigelse bemyndigelse = new Bemyndigelse();

        bemyndigelse.setBemyndigedeCpr(bemyndigedeCpr);

        final Rettighed rettighed = new Rettighed();
        rettighed.setRettighedskode("TEST rettighed");
        bemyndigelse.setRettighed(rettighed);

        final LinkedSystem linkedSystem = new LinkedSystem();
        linkedSystem.setSystem("TEST system");
        bemyndigelse.setLinkedSystem(linkedSystem);

        return bemyndigelse;
    }

    private BemyndigelserType bemyndigelserEq(final List<Bemyndigelse> bemyndigelser) {
        System.out.println("Cheking bemyndigelser");
        return argThat(new TypeSafeMatcher<BemyndigelserType>() {
            @Override
            public boolean matchesSafely(BemyndigelserType item) {
                if (!item.getAntalPost().equals(BigInteger.valueOf(bemyndigelser.size()))) {
                    System.out.println("Not same size");
                    return false;
                }
                //TODO: more precise asserttion

                return true;
            }

            @Override
            public void describeTo(Description description) {
            }
        });
    }

}
