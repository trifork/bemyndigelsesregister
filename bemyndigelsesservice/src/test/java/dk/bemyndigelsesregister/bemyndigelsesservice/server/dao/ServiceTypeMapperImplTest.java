package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean.RoleDaoEbean;
import dk.nsi.bemyndigelse._2012._05._01.Arbejdsfunktioner;
import dk.nsi.bemyndigelse._2012._05._01.DelegerbarRettigheder;
import dk.nsi.bemyndigelse._2012._05._01.Rettigheder;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServiceTypeMapperImplTest {
    @InjectMocks
    private ServiceTypeMapperImpl typeMapper = new ServiceTypeMapperImpl();

    @Mock
    RoleDao roleDao;

    @Mock
    DelegatingSystemDao delegatingSystemDao;

    @Mock
    PermissionDao permissionDao;

    private final Domaene testDomaene = new Domaene() {{
        setKode("DomæneKode");
    }};
    private final LinkedSystem testLinkedSystem = new LinkedSystem() {{
        setKode("LinkedSystemKode");
        setDomaene(testDomaene);
    }};
    private final Arbejdsfunktion testArbejdsfunktion = new Arbejdsfunktion() {{
        setKode("ArbejdsfunktionKode");
        setLinkedSystem(testLinkedSystem);
    }};

    private final Rettighed testRettighed = new Rettighed() {{
        setKode("RettighedKode");
        setLinkedSystem(testLinkedSystem);
    }};

    private String domainId = "UUID kode";
    private String delegatorCpr = "BemyndigendeCpr";
    private String delegateeCpr = "BemyndigedeCpr";
    private String delegateeCvr = "BemyndigedeCvr";
    private String roleId = "Arbejdsfunktionskode";
    private String systemId = "SystemKode";
    private String permissionId1 = "P1";
    private String permissionId2 = "P2";
    private List<String> permissionIds = Arrays.asList(permissionId1, permissionId2);
    private State state = State.GODKENDT;
    private DateTime now = DateTime.now();

    @Before public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void willMapToArbejdsfunktioner() throws Exception {
        final Arbejdsfunktion arbejdsfunktion = new Arbejdsfunktion() {{
            this.setKode("Kode");
            this.setBeskrivelse("Beskrivelse");
            this.setLinkedSystem(testLinkedSystem);
        }};
        final Arbejdsfunktioner jaxbArbejdsfunktioner = typeMapper.toJaxbArbejdsfunktioner(asList(arbejdsfunktion));

        assertEquals(1, jaxbArbejdsfunktioner.getArbejdsfunktion().size());
        final Arbejdsfunktioner.Arbejdsfunktion jaxbArbejdsfunktion = jaxbArbejdsfunktioner.getArbejdsfunktion().get(0);

        assertEquals(arbejdsfunktion.getKode(), jaxbArbejdsfunktion.getArbejdsfunktion());
        assertEquals(arbejdsfunktion.getBeskrivelse(), jaxbArbejdsfunktion.getBeskrivelse());
        assertEquals(arbejdsfunktion.getLinkedSystem().getDomaene().getKode(), jaxbArbejdsfunktion.getDomaene());
        assertEquals(arbejdsfunktion.getLinkedSystem().getKode(), jaxbArbejdsfunktion.getSystem());
    }

    @Test
    public void willMapToRettigheder() throws Exception {
        final Rettighed rettighed = new Rettighed() {{
            this.setBeskrivelse("Beskrivelse");
            this.setLinkedSystem(testLinkedSystem);
            this.setKode("Kode");
        }};

        final Rettigheder jaxbRettigheder = typeMapper.toJaxbRettigheder(asList(rettighed));

        assertEquals(1, jaxbRettigheder.getRettighed().size());
        final Rettigheder.Rettighed jaxbRettighed = jaxbRettigheder.getRettighed().get(0);

        assertEquals(rettighed.getBeskrivelse(), jaxbRettighed.getBeskrivelse());
        assertEquals(rettighed.getLinkedSystem().getDomaene().getKode(), jaxbRettighed.getDomaene());
        assertEquals(rettighed.getLinkedSystem().getKode(), jaxbRettighed.getSystem());
        assertEquals(rettighed.getKode(), jaxbRettighed.getRettighed());
    }

    @Test
    public void willMapToDelegerbarRettigheder() throws Exception {
        final DelegerbarRettighed delegerbarRettighed = new DelegerbarRettighed() {{
            this.setArbejdsfunktion(testArbejdsfunktion);
            this.setRettighedskode(testRettighed);
        }};

        final DelegerbarRettigheder jaxbDelegerbarRettigheder = typeMapper.toJaxbDelegerbarRettigheder(asList(delegerbarRettighed));

        assertEquals(1, jaxbDelegerbarRettigheder.getDelegerbarRettighed().size());
        final DelegerbarRettigheder.DelegerbarRettighed jaxbRettighed = jaxbDelegerbarRettigheder.getDelegerbarRettighed().get(0);

        assertEquals(delegerbarRettighed.getArbejdsfunktion().getKode(), jaxbRettighed.getArbejdsfunktion());
        assertEquals(delegerbarRettighed.getArbejdsfunktion().getLinkedSystem().getDomaene().getKode(), jaxbRettighed.getDomaene());
        assertEquals(delegerbarRettighed.getRettighedskode().getKode(), jaxbRettighed.getRettighed());
        assertEquals(delegerbarRettighed.getArbejdsfunktion().getLinkedSystem().getKode(), jaxbRettighed.getSystem());
    }

    @Test
    public void willMapToDelegation() throws Exception {
        dk.nsi.bemyndigelse._2016._01._01.Delegation d = typeMapper.toDelegationType(createDelegation());

        assertNotNull(d);
        assertEquals(domainId, d.getDelegationId());
        assertEquals(delegatorCpr, d.getDelegatorCpr());
        assertEquals(delegateeCpr, d.getDelegateeCpr());
        assertEquals(delegateeCvr, d.getDelegateeCvr());
        assertEquals(dk.nsi.bemyndigelse._2016._01._01.State.GODKENDT, d.getState());
        assertNotNull(d.getCreated());
        assertNotNull(d.getEffectiveFrom());
        assertNotNull(d.getEffectiveTo());
    }

    private Delegation createDelegation() {
        Delegation delegation = new Delegation();

        delegation.setDomainId(domainId);
        delegation.setDelegatorCpr(delegatorCpr);
        delegation.setDelegateeCpr(delegateeCpr);
        delegation.setDelegateeCvr(delegateeCvr);
        delegation.setDelegatingSystem(systemId);
        delegation.setRole(roleId);
        delegation.setState(state);

        Set<DelegationPermission> permissionList = new HashSet<>();
        for (String permissionId : permissionIds) {
            final DelegationPermission permission = new DelegationPermission();
            permission.setDelegation(delegation);
            permission.setPermissionId(permissionId);
            permissionList.add(permission);
        }
        delegation.setDelegationPermissions(permissionList);

        delegation.setCreated(now);
        delegation.setEffectiveFrom(now.minusDays(1));
        delegation.setEffectiveTo(now.plusDays(1));

        return delegation;
    }
}
