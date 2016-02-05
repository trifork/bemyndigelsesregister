package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import com.avaje.ebean.EbeanServer;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.State;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean.DaoUnitTestSupport;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * BEM 2.0 bemyndigelse
 * Created by obj on 03-02-2016.
 */
public class DelegationManagerImplTest extends DaoUnitTestSupport {
    @Inject
    DelegationManager manager;

    @Inject
    EbeanServer ebeanServer;

    final String delegatorCpr = "1111111111";
    final String delegateeCpr = "2222222222";
    final String delegateeCvr = "3333333333";
    final String roleCode = "Laege";
    final String permissionCode1 = "R01";
    final String permissionCode2 = "R02";
    final String systemKode = "triforktest";
    final LinkedSystem linkedSystem = LinkedSystem.createForTest(systemKode);

    {
        linkedSystem.setKode("SystemKode");
    }

    final DateTime dato0 = new DateTime(2014, 1, 1, 10, 0, 0);
    final DateTime dato1 = new DateTime(2015, 1, 1, 10, 0, 0);
    final DateTime dato2 = new DateTime(2015, 1, 10, 10, 0, 0);

    @Test
    public void testCreateOverlappingDelegation1() throws Exception {
        try {
            ebeanServer.beginTransaction();

            // create delegation valid from dato1
            Delegation delegation = manager.createDelegation(systemKode, delegatorCpr, delegateeCpr, delegateeCvr, roleCode, State.BESTILT, Arrays.asList(permissionCode1, permissionCode2), dato1, null);

            // create delegation valid from dato2
            manager.createDelegation(systemKode, delegatorCpr, delegateeCpr, delegateeCvr, roleCode, State.BESTILT, Arrays.asList(permissionCode1, permissionCode2), dato2, null);

            delegation = manager.getDelegation(delegation.getDomainId()); // reload first delegation

            assertEquals("Først oprettede bemyndigelse skal være afsluttet på samme tidspunkt som sidst oprettede bemyndigelse starter når perioder overlapper", dato2, delegation.getEffectiveTo());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testCreateOverlappingDelegation2() throws Exception {
        try {
            ebeanServer.beginTransaction();

            // create delegation valid from dato1
            Delegation delegation = manager.createDelegation(systemKode, delegatorCpr, delegateeCpr, delegateeCvr, roleCode, State.BESTILT, Arrays.asList(permissionCode1, permissionCode2), dato1, null);

            // create delegation valid from before the first
            manager.createDelegation(systemKode, delegatorCpr, delegateeCpr, delegateeCvr, roleCode, State.BESTILT, Arrays.asList(permissionCode1, permissionCode2), dato0, null);

            delegation = manager.getDelegation(delegation.getDomainId()); // reload first delegation

            assertEquals("Først oprettede bemyndigelse skal være afsluttet på samme tidspunkt som den starter", dato1, delegation.getEffectiveTo());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testDeleteDelegation() throws Exception {
        try {
            ebeanServer.beginTransaction();

            Delegation delegation = manager.createDelegation(systemKode, delegatorCpr, delegateeCpr, delegateeCvr, roleCode, State.BESTILT, Arrays.asList(permissionCode1, permissionCode2), dato1, null);
            String uuid = manager.deleteDelegation(delegation.getDomainId(), dato2);
            delegation = manager.getDelegation(uuid); // reload delegation

            assertEquals("Bemyndigelse skal være afsluttet", dato2, delegation.getEffectiveTo());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteDelegationBadDate() throws Exception {
        try {
            ebeanServer.beginTransaction();

            Delegation delegation = manager.createDelegation(systemKode, delegatorCpr, delegateeCpr, delegateeCvr, roleCode, State.BESTILT, Arrays.asList(permissionCode1, permissionCode2), dato1, null);
            manager.deleteDelegation(delegation.getDomainId(), dato0); // should fail before date0 is before date1
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testFindDelegationByDelegator() {
        try {
            ebeanServer.beginTransaction();

            manager.createDelegation(systemKode, delegatorCpr, delegateeCpr, delegateeCvr, roleCode, State.BESTILT, Arrays.asList(permissionCode1, permissionCode2), dato1, null);
            manager.createDelegation(systemKode, delegatorCpr, delegateeCpr, delegateeCvr, roleCode, State.GODKENDT, Arrays.asList(permissionCode1, permissionCode2), dato1, null);
            List<Delegation> list = manager.getDelegationsByDelegatorCpr(delegatorCpr);

            assertNotNull("Der skal returneres en liste af bemyndigelser for bemyndigende cpr " + delegatorCpr, list);
            assertEquals("Der skal findes 2 bemyndigelser for bemyndigende cpr " + delegatorCpr, 2, list.size());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testFindDelegationByDelegatee() {
        try {
            ebeanServer.beginTransaction();

            manager.createDelegation(systemKode, delegatorCpr, delegateeCpr, delegateeCvr, roleCode, State.BESTILT, Arrays.asList(permissionCode1, permissionCode2), dato1, null);
            manager.createDelegation(systemKode, delegatorCpr, delegateeCpr, delegateeCvr, roleCode, State.BESTILT, Arrays.asList(permissionCode1, permissionCode2), dato2, null);
            List<Delegation> list = manager.getDelegationsByDelegateeCpr(delegateeCpr);

            assertNotNull("Der skal returneres en liste af bemyndigelser for bemyndigede cpr " + delegateeCpr, list);
            assertEquals("Der skal findes 2 bemyndigelser for bemyndigende cpr " + delegateeCpr, 2, list.size());
        } finally {
            ebeanServer.endTransaction();
        }
    }
}
