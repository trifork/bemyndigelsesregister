package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import com.avaje.ebean.EbeanServer;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.TestData;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean.DaoUnitTestSupport;
import dk.nsi.bemyndigelse._2016._01._01.State;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

    final DateTime dato0 = new DateTime(2014, 1, 1, 10, 0, 0);
    final DateTime dato1 = new DateTime(2015, 1, 1, 10, 0, 0);
    final DateTime dato2 = new DateTime(2015, 1, 10, 10, 0, 0);

    @Test
    public void testCreateOverlappingDelegation1() throws Exception {
        try {
            ebeanServer.beginTransaction();

            // create delegation valid from dato1
            Delegation delegation = manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, State.BESTILT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), dato1, null);

            // create delegation valid from dato2
            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, State.BESTILT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), dato2, null);

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
            Delegation delegation = manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, State.BESTILT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), dato1, null);

            // create delegation valid from before the first
            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, State.BESTILT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), dato0, null);

            delegation = manager.getDelegation(delegation.getDomainId()); // reload first delegation

            assertEquals("Først oprettede bemyndigelse skal være afsluttet på samme tidspunkt som den starter", dato1, delegation.getEffectiveTo());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testDeleteDelegationAsDelegator() throws Exception {
        try {
            ebeanServer.beginTransaction();

            Delegation delegation = manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, State.BESTILT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), dato1, null);
            String uuid = manager.deleteDelegation(delegatorCpr, null, delegation.getDomainId(), dato2);
            delegation = manager.getDelegation(uuid); // reload delegation

            assertEquals("Bemyndigelse skal være afsluttet", dato2, delegation.getEffectiveTo());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testDeleteDelegationAsDelegatee() throws Exception {
        try {
            ebeanServer.beginTransaction();

            Delegation delegation = manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, State.BESTILT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), dato1, null);
            String uuid = manager.deleteDelegation(null, delegateeCpr, delegation.getDomainId(), dato2);
            delegation = manager.getDelegation(uuid); // reload delegation

            assertEquals("Bemyndigelse skal være afsluttet", dato2, delegation.getEffectiveTo());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testDeleteNonExistingDelegation() throws Exception {
        try {
            ebeanServer.beginTransaction();

            String uuid = manager.deleteDelegation(null, delegateeCpr, "non-existing-delegation-id", dato2);

            assertNull("Sletning af en ikke eksisterende bemyndigelse skal give null", uuid);
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testDeleteThirdPartyDelegation() throws Exception {
        try {
            ebeanServer.beginTransaction();

            Delegation delegation = manager.createDelegation(TestData.systemCode, "anotherCpr", delegateeCpr, delegateeCvr, TestData.roleCode, State.BESTILT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), dato1, null);
            String delegationId = delegation.getDomainId();

            Delegation loadedDelegation = manager.getDelegation(delegationId); // reload delegation

            String uuid = manager.deleteDelegation(delegatorCpr, null, delegationId, dato2);
            assertNull("Sletning af en ikke eksisterende bemyndigelse skal give null", uuid);

            Delegation reloadedDelegation = manager.getDelegation(delegationId); // reload delegation

            assertEquals("Bemyndigelse skal være uforandret", loadedDelegation.getDomainId(), reloadedDelegation.getDomainId());
            assertEquals("Bemyndigelse skal være uforandret", loadedDelegation.getEffectiveTo(), reloadedDelegation.getEffectiveTo());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteDelegationBadDate() throws Exception {
        try {
            ebeanServer.beginTransaction();

            Delegation delegation = manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, State.BESTILT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), dato1, null);
            manager.deleteDelegation(delegatorCpr, null, delegation.getDomainId(), dato0); // should fail before date0 is before date1
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testFindDelegationByDelegator() {
        try {
            ebeanServer.beginTransaction();

            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, State.BESTILT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), dato1, null);
            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, State.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), dato1, null);
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

            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, State.BESTILT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), dato1, null);
            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, State.BESTILT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), dato2, null);
            List<Delegation> list = manager.getDelegationsByDelegateeCpr(delegateeCpr);

            assertNotNull("Der skal returneres en liste af bemyndigelser for bemyndigede cpr " + delegateeCpr, list);
            assertEquals("Der skal findes 2 bemyndigelser for bemyndigende cpr " + delegateeCpr, 2, list.size());
        } finally {
            ebeanServer.endTransaction();
        }
    }
}