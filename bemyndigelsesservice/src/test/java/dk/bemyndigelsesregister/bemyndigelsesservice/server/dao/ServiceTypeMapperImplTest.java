package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.nsi.bemyndigelse._2012._05._01.Arbejdsfunktioner;
import dk.nsi.bemyndigelse._2012._05._01.DelegerbarRettigheder;
import dk.nsi.bemyndigelse._2012._05._01.Rettigheder;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class ServiceTypeMapperImplTest {
    final ServiceTypeMapper typeMapper = new ServiceTypeMapperImpl();
    final Domaene testDomaene = new Domaene() {{
        setKode("DomæneKode");
    }};
    final LinkedSystem testLinkedSystem = new LinkedSystem() {{
        setSystem("LinkedSystemKode");
    }};
    final Arbejdsfunktion testArbejdsfunktion = new Arbejdsfunktion() {{
        setKode("ArbejdsfunktionKode");
    }};


    @Test
    public void willMapToArbejdsfunktioner() throws Exception {
        final Arbejdsfunktion arbejdsfunktion = new Arbejdsfunktion() {{
            this.setKode("Kode");
            this.setBeskrivelse("Beskrivelse");
            this.setDomaene(testDomaene);
            this.setLinkedSystem(testLinkedSystem);
        }};
        final Arbejdsfunktioner jaxbArbejdsfunktioner = typeMapper.toJaxbArbejdsfunktioner(asList(arbejdsfunktion));

        assertEquals(1, jaxbArbejdsfunktioner.getArbejdsfunktion().size());
        final Arbejdsfunktioner.Arbejdsfunktion jaxbArbejdsfunktion = jaxbArbejdsfunktioner.getArbejdsfunktion().get(0);

        assertEquals(arbejdsfunktion.getKode(), jaxbArbejdsfunktion.getArbejdsfunktion());
        assertEquals(arbejdsfunktion.getBeskrivelse(), jaxbArbejdsfunktion.getBeskrivelse());
        assertEquals(arbejdsfunktion.getDomaene().getKode(), jaxbArbejdsfunktion.getDomaene());
        assertEquals(arbejdsfunktion.getLinkedSystem().getSystem(), jaxbArbejdsfunktion.getSystem());
    }

    @Test
    public void willMapToRettigheder() throws Exception {
        final Rettighed rettighed = new Rettighed() {{
            this.setBeskrivelse("Beskrivelse");
            this.setDomaene(testDomaene);
            this.setLinkedSystem(testLinkedSystem);
            this.setRettighedskode("Kode");
        }};

        final Rettigheder jaxbRettigheder = typeMapper.toJaxbRettigheder(asList(rettighed));

        assertEquals(1, jaxbRettigheder.getRettighed().size());
        final Rettigheder.Rettighed jaxbRettighed = jaxbRettigheder.getRettighed().get(0);

        assertEquals(rettighed.getBeskrivelse(), jaxbRettighed.getBeskrivelse());
        assertEquals(rettighed.getDomaene().getKode(), jaxbRettighed.getDomaene());
        assertEquals(rettighed.getLinkedSystem().getSystem(), jaxbRettighed.getSystem());
        assertEquals(rettighed.getRettighedskode(), jaxbRettighed.getRettighed());
    }
    @Test
    public void willMapToDelegerbarRettigheder() throws Exception {
        final DelegerbarRettighed delegerbarRettighed = new DelegerbarRettighed() {{
            this.setArbejdsfunktion(testArbejdsfunktion);
            this.setDomaene(testDomaene);
            this.setKode("Kode");
            this.setSystem(testLinkedSystem);
        }};

        final DelegerbarRettigheder jaxbDelegerbarRettigheder = typeMapper.toJaxbDelegerbarRettigheder(asList(delegerbarRettighed));

        assertEquals(1, jaxbDelegerbarRettigheder.getDelegerbarRettighed().size());
        final DelegerbarRettigheder.DelegerbarRettighed jaxbRettighed = jaxbDelegerbarRettigheder.getDelegerbarRettighed().get(0);

        assertEquals(delegerbarRettighed.getArbejdsfunktion().getKode(), jaxbRettighed.getArbejdsfunktion());
        assertEquals(delegerbarRettighed.getDomaene().getKode(), jaxbRettighed.getDomaene());
        assertEquals(delegerbarRettighed.getKode(), jaxbRettighed.getRettighed());
        assertEquals(delegerbarRettighed.getSystem().getSystem(), jaxbRettighed.getSystem());
    }


}
