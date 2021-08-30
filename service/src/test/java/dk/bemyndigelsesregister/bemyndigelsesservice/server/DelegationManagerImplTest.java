package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import com.avaje.ebean.EbeanServer;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Metadata;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Status;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.TestData;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean.DaoUnitTestSupport;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * BEM 2.0 bemyndigelse
 * Created by obj on 03-02-2016.
 */
public class DelegationManagerImplTest extends DaoUnitTestSupport {
    @Inject
    DelegationManager manager;

    @Inject
    MetadataManager metadataManager;

    @Inject
    EbeanServer ebeanServer;

    final String delegatorCpr = "1111111111";
    final String delegateeCpr = "2222222222";
    final String delegateeCvr = "3333333333";

    final DateTime date0 = new DateTime(2014, 1, 1, 10, 0, 0);
    final DateTime date1 = new DateTime(2015, 1, 1, 10, 0, 0);
    final DateTime date2 = new DateTime(2015, 1, 10, 10, 0, 0);
    final DateTime date3 = new DateTime(2016, 1, 10, 10, 0, 0);

    @Before
    public void setUp() {
        metadataManager.clearCache();
    }

    @Test
    public void testCreateOverlappingDelegation() {
        try {
            ebeanServer.beginTransaction();

            // create delegation valid from date1
            Delegation delegation = manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);

            // create delegation valid from date2
            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date2, null);

            delegation = manager.getDelegation(delegation.getCode()); // reload first delegation

            assertEquals("Først oprettede bemyndigelse skal være afsluttet på samme tidspunkt som sidst oprettede bemyndigelse starter når perioder overlapper", date2, delegation.getEffectiveTo());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testCreateApprovedClosesOverlappingRequested() {
        try {
            ebeanServer.beginTransaction();

            Delegation delegation = manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date2, null);
            delegation = manager.getDelegation(delegation.getCode()); // reload first delegation

            assertEquals("Eksisterende anmodet bemyndigelse skal være afsluttet på samme tidspunkt som godkendt bemyndigelse starter når perioder overlapper", date2, delegation.getEffectiveTo());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testCreateApprovedClosesOverlappingApproved() {
        try {
            ebeanServer.beginTransaction();

            Delegation delegation = manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date2, null);
            delegation = manager.getDelegation(delegation.getCode()); // reload first delegation

            assertEquals("Eksisteerende godkendt bemyndigelse skal være afsluttet på samme tidspunkt som godkendt bemyndigelse starter når perioder overlapper", date2, delegation.getEffectiveTo());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testCreateRequestedDoesNotCloseOverlappingApproved() {
        try {
            ebeanServer.beginTransaction();

            Delegation delegation = manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
            delegation = manager.getDelegation(delegation.getCode()); // reload first delegation
            DateTime effectiveTo = delegation.getEffectiveTo();

            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date2, null);
            delegation = manager.getDelegation(delegation.getCode()); // reload first delegation

            assertEquals("Eksisterende godkendt bemyndigelse skal ikke påvirkes af ny anmodet bemyndigelse selvom perioder overlapper", effectiveTo, delegation.getEffectiveTo());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testCreateDelegationWithAsteriskPermission() {
        try {
            ebeanServer.beginTransaction();

            Delegation delegation = manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.GODKENDT, Arrays.asList(Metadata.ASTERISK_PERMISSION_CODE, TestData.permissionCode1), date1, null);
            delegation = manager.getDelegation(delegation.getCode()); // reload delegation

            assertNotNull("Der skal findes en bemyndigelse", delegation);
            assertNotNull("Der skal findes rettigheder i bemyndigelsen", delegation.getDelegationPermissions());
            assertEquals("Der skal være 3 lagrede rettigheder i bemyndigelsen pga. *", 3, delegation.getDelegationPermissions().size());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testDeleteDelegationAsDelegator() {
        try {
            ebeanServer.beginTransaction();

            Delegation delegation = manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
            String uuid = manager.deleteDelegation(delegatorCpr, null, delegation.getCode(), date2);
            delegation = manager.getDelegation(uuid); // reload delegation

            assertEquals("Bemyndigelse skal være afsluttet", date2, delegation.getEffectiveTo());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testDeleteDelegationAsDelegatee() {
        try {
            ebeanServer.beginTransaction();

            Delegation delegation = manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
            String uuid = manager.deleteDelegation(null, delegateeCpr, delegation.getCode(), date2);
            delegation = manager.getDelegation(uuid); // reload delegation

            assertEquals("Bemyndigelse skal være afsluttet", date2, delegation.getEffectiveTo());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testDeleteNonExistingDelegation() {
        try {
            ebeanServer.beginTransaction();

            String uuid = manager.deleteDelegation(null, delegateeCpr, "non-existing-delegation-id", date2);

            assertNull("Sletning af en ikke eksisterende bemyndigelse skal give null", uuid);
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testDeleteThirdPartyDelegation() {
        try {
            ebeanServer.beginTransaction();

            Delegation delegation = manager.createDelegation(TestData.systemCode, "anotherCpr", delegateeCpr, delegateeCvr, TestData.roleCode, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
            String delegationId = delegation.getCode();

            Delegation loadedDelegation = manager.getDelegation(delegationId); // reload delegation

            String uuid = manager.deleteDelegation(delegatorCpr, null, delegationId, date2);
            assertNull("Sletning af en ikke eksisterende bemyndigelse skal give null", uuid);

            Delegation reloadedDelegation = manager.getDelegation(delegationId); // reload delegation

            assertEquals("Bemyndigelse skal være uforandret", loadedDelegation.getCode(), reloadedDelegation.getCode());
            assertEquals("Bemyndigelse skal være uforandret", loadedDelegation.getEffectiveTo(), reloadedDelegation.getEffectiveTo());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteDelegationBadDate() {
        try {
            ebeanServer.beginTransaction();

            Delegation delegation = manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date0, date1);
            manager.deleteDelegation(delegatorCpr, null, delegation.getCode(), date2); // should fail because date2 is after date1
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testFindDelegationByDelegator() {
        try {
            ebeanServer.beginTransaction();

            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
            List<Delegation> list = manager.getDelegationsByDelegatorCpr(delegatorCpr);

            assertNotNull("Der skal returneres en liste af bemyndigelser for bemyndigende cpr " + delegatorCpr, list);
            assertEquals("Der skal findes 2 bemyndigelser for bemyndigende cpr " + delegatorCpr, 2, list.size());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testFindDelegationByDelegatorAndFromDate() {
        try {
            ebeanServer.beginTransaction();

            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date0, date1);
            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, date2);
            List<Delegation> list = manager.getDelegationsByDelegatorCpr(delegatorCpr, date1, null);

            assertNotNull("Der skal returneres en liste af bemyndigelser for bemyndigende cpr " + delegatorCpr, list);
            assertEquals("Der skal findes 1 bemyndigelse for bemyndigende cpr " + delegatorCpr + ", som er gyldig efter " + date1, 1, list.size());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testFindDelegationByDelegatorAndToDate() {
        try {
            ebeanServer.beginTransaction();

            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date0, date1);
            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date2, date3);
            List<Delegation> list = manager.getDelegationsByDelegatorCpr(delegatorCpr, null, date1);

            assertNotNull("Der skal returneres en liste af bemyndigelser for bemyndigende cpr " + delegatorCpr, list);
            assertEquals("Der skal findes 1 bemyndigelse for bemyndigende cpr " + delegatorCpr + ", som er gyldig indtil " + date1, 1, list.size());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testFindDelegationByDelegatorAndPeriod() {
        try {
            ebeanServer.beginTransaction();

            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date0, date1);
            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date2, date3);
            List<Delegation> list = manager.getDelegationsByDelegatorCpr(delegatorCpr, date0, date3);

            assertNotNull("Der skal returneres en liste af bemyndigelser for bemyndigende cpr " + delegatorCpr, list);
            assertEquals("Der skal findes 2 bemyndigelser for bemyndigende cpr " + delegatorCpr + ", som er gyldige fra " + date0 + " til " + date3, 2, list.size());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testFindDelegationByDelegatee() {
        try {
            ebeanServer.beginTransaction();

            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date2, null);
            List<Delegation> list = manager.getDelegationsByDelegateeCpr(delegateeCpr);

            assertNotNull("Der skal returneres en liste af bemyndigelser for bemyndigede cpr " + delegateeCpr, list);
            assertEquals("Der skal findes 2 bemyndigelser for bemyndigende cpr " + delegateeCpr, 2, list.size());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testFindDelegationsWithRemovedMetadata() {
        try {
            ebeanServer.beginTransaction();

            List<String> permissionCodes = Arrays.asList(TestData.permissionCode1, TestData.permissionCode2);
            manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, Status.GODKENDT, permissionCodes, date1, null);

            // remove permissionCode2 from metadata
            Metadata metadata = metadataManager.getMetadata(TestData.domainCode, TestData.systemCode);
            metadata.getPermissions().clear();
            metadata.addPermission(TestData.permissionCode1, TestData.permissionDescription1);
            metadata.getDelegatablePermissions().clear();
            metadata.addDelegatablePermission(TestData.roleCode, TestData.permissionCode1, TestData.permissionDescription1, true);
            metadataManager.putMetadata(metadata);

            List<Delegation> list = manager.getDelegationsByDelegatorCpr(delegatorCpr);

            assertNotNull("Der skal returneres en liste af bemyndigelser for bemyndigende cpr " + delegatorCpr, list);
            assertEquals("Der skal findes 1 bemyndigelse for bemyndigende cpr " + delegatorCpr, 1, list.size());

            Delegation delegation = list.get(0);
            assertEquals("Der skal findes 2 rettigheder for bemyndigelse", 2, delegation.getDelegationPermissions().size());

            Set<String> expectedCodes = new HashSet<>(permissionCodes);
            for (DelegationPermission dp : delegation.getDelegationPermissions())
                expectedCodes.remove(dp.getPermissionCode());
            assertTrue("Både " + TestData.permissionCode1 + " og " + TestData.permissionCode2 + " forventes returneret selvom metadata mangler for den ene", expectedCodes.isEmpty());
        } finally {
            ebeanServer.endTransaction();
        }
    }
}
