package dk.bemyndigelsesregister.bemyndigelsesservice.server;

public class BemyndigelsesExportJobTest {
/* TODO OBJ Implement tests with new BEM2 service
    BemyndigelsesExportJob job = new BemyndigelsesExportJob();
    BemyndigelseDao bemyndigelseDao = mock(BemyndigelseDao.class);
    private final SystemVariableDao systemVariableDao = Mockito.mock(SystemVariableDao.class);

    NspManager nspManager = mock(NspManager.class);
    private final SystemService systemService = Mockito.mock(SystemService.class);

    @Before
    public void setUp() throws Exception {
        job.nspManager = nspManager;
        job.bemyndigelseDao = bemyndigelseDao;
        job.systemVariableDao = systemVariableDao;
        job.systemService = systemService;
    }

    @Test
    public void willSendBemyndigelserToNsp() throws Exception {
        final List<Bemyndigelse> bemyndigelser = asList(createBemyndigelse("bemyndigede cpr 1"), createBemyndigelse("bemyndigede cpr 2"));
        final DateTime startTime = new DateTime();
        final DateTime lastRun = new DateTime(0l);

        final SystemVariable lastRunSV = new SystemVariable("lastRun", lastRun);
        when(systemVariableDao.getByName("lastRun")).thenReturn(lastRunSV);
        when(systemService.getDateTime()).thenReturn(startTime);
        when(bemyndigelseDao.findBySidstModificeretGreaterThanOrEquals(lastRun)).thenReturn(bemyndigelser);

        job.startExport();

        verify(nspManager).send(bemyndigelserEq(bemyndigelser), eq(startTime));
        verify(systemVariableDao).save(lastRunSV);
    }

    @Test
    public void canRunCompleteExport() throws Exception {
        final DateTime startTime = new DateTime();
        final List<Bemyndigelse> bemyndigelser = asList(createBemyndigelse("bemyndigede cpr 1"), createBemyndigelse("bemyndigede cpr 2"));

        final SystemVariable lastRunSV = new SystemVariable("lastRun", new DateTime(0l));
        when(systemVariableDao.getByName("lastRun")).thenReturn(lastRunSV);
        when(systemService.getDateTime()).thenReturn(startTime);
        when(bemyndigelseDao.list()).thenReturn(bemyndigelser);

        job.completeExport();

        verify(nspManager).send(bemyndigelserEq(bemyndigelser), eq(startTime));
        verify(systemVariableDao).save(lastRunSV);
    }

    private Bemyndigelse createBemyndigelse(String bemyndigedeCpr) {
        final Bemyndigelse bemyndigelse = new Bemyndigelse();

        bemyndigelse.setStatus(Status.GODKENDT);
        bemyndigelse.setBemyndigedeCpr(bemyndigedeCpr);

        final Rettighed rettighed = new Rettighed();
        rettighed.setKode("TEST rettighed");
        bemyndigelse.setRettighedKode(rettighed.getKode());

        final Arbejdsfunktion arbejdsfunktion = new Arbejdsfunktion();
        arbejdsfunktion.setKode("Test arbejdsfunktion");
        bemyndigelse.setArbejdsfunktionKode(arbejdsfunktion.getKode());

        final LinkedSystem linkedSystem = new LinkedSystem();
        linkedSystem.setKode("TEST system");
        bemyndigelse.setLinkedSystemKode(linkedSystem.getKode());

        bemyndigelse.setSidstModificeret(new DateTime());
        bemyndigelse.setSidstModificeretAf("Test");

        return bemyndigelse;
    }

    private Bemyndigelser bemyndigelserEq(final List<Bemyndigelse> bemyndigelser) {
        System.out.println("Cheking bemyndigelser");
        return argThat(new TypeSafeMatcher<Bemyndigelser>() {
            @Override
            public boolean matchesSafely(Bemyndigelser item) {
                if (!item.getAntalPost().equals(BigInteger.valueOf(bemyndigelser.size()))) {
                    System.out.println("Not same size");
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
            }
        });
    }
*/
}
