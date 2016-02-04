package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import com.avaje.ebean.EbeanServer;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse20;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Status;
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
public class Bemyndigelse20ManagerImplTest extends DaoUnitTestSupport {
    @Inject
    Bemyndigelse20Manager manager;

    @Inject
    EbeanServer ebeanServer;

    final String bemyndigendeCpr = "1111111111";
    final String bemyndigedeCpr = "2222222222";
    final String bemyndigedeCvr = "3333333333";
    final String arbejdsfunktionKode = "Laege";
    final String rettighedKode1 = "R01";
    final String rettighedKode2 = "R02";
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
            Bemyndigelse20 bemyndigelse = manager.createDelegation(systemKode, bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, Status.BESTILT, Arrays.asList(rettighedKode1, rettighedKode2), dato1, null);

            // create delegation valid from dato2
            manager.createDelegation(systemKode, bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, Status.BESTILT, Arrays.asList(rettighedKode1, rettighedKode2), dato2, null);

            bemyndigelse = manager.getDelegation(bemyndigelse.getKode()); // reload first delegation

            assertEquals("Først oprettede bemyndigelse skal være afsluttet på samme tidspunkt som sidst oprettede bemyndigelse starter når perioder overlapper", dato2, bemyndigelse.getGyldigTil());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testCreateOverlappingDelegation2() throws Exception {
        try {
            ebeanServer.beginTransaction();

            // create delegation valid from dato1
            Bemyndigelse20 bemyndigelse = manager.createDelegation(systemKode, bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, Status.BESTILT, Arrays.asList(rettighedKode1, rettighedKode2), dato1, null);

            // create delegation valid from before the first
            manager.createDelegation(systemKode, bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, Status.BESTILT, Arrays.asList(rettighedKode1, rettighedKode2), dato0, null);

            bemyndigelse = manager.getDelegation(bemyndigelse.getKode()); // reload first delegation

            assertEquals("Først oprettede bemyndigelse skal være afsluttet på samme tidspunkt som den starter", dato1, bemyndigelse.getGyldigTil());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testDeleteDelegation() throws Exception {
        try {
            ebeanServer.beginTransaction();

            Bemyndigelse20 bemyndigelse = manager.createDelegation(systemKode, bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, Status.BESTILT, Arrays.asList(rettighedKode1, rettighedKode2), dato1, null);
            String uuid = manager.deleteDelegation(bemyndigelse.getKode(), dato2);
            bemyndigelse = manager.getDelegation(uuid); // reload delegation

            assertEquals("Bemyndigelse skal være afsluttet", dato2, bemyndigelse.getGyldigTil());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteDelegationBadDate() throws Exception {
        try {
            ebeanServer.beginTransaction();

            Bemyndigelse20 bemyndigelse = manager.createDelegation(systemKode, bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, Status.BESTILT, Arrays.asList(rettighedKode1, rettighedKode2), dato1, null);
            manager.deleteDelegation(bemyndigelse.getKode(), dato0); // should fail before date0 is before date1
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testFindDelegationByDelegator() {
        try {
            ebeanServer.beginTransaction();

            manager.createDelegation(systemKode, bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, Status.BESTILT, Arrays.asList(rettighedKode1, rettighedKode2), dato1, null);
            manager.createDelegation(systemKode, bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, Status.GODKENDT, Arrays.asList(rettighedKode1, rettighedKode2), dato1, null);
            List<Bemyndigelse20> list = manager.getDelegationsByDelegatorCpr(bemyndigendeCpr);

            assertNotNull("Der skal returneres en liste af bemyndigelser for bemyndigende cpr " + bemyndigendeCpr, list);
            assertEquals("Der skal findes 2 bemyndigelser for bemyndigende cpr " + bemyndigendeCpr, 2, list.size());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testFindDelegationByDelegatee() {
        try {
            ebeanServer.beginTransaction();

            manager.createDelegation(systemKode, bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, Status.BESTILT, Arrays.asList(rettighedKode1, rettighedKode2), dato1, null);
            manager.createDelegation(systemKode, bemyndigendeCpr, bemyndigedeCpr, bemyndigedeCvr, arbejdsfunktionKode, Status.BESTILT, Arrays.asList(rettighedKode1, rettighedKode2), dato2, null);
            List<Bemyndigelse20> list = manager.getDelegationsByDelegateeCpr(bemyndigedeCpr);

            assertNotNull("Der skal returneres en liste af bemyndigelser for bemyndigede cpr " + bemyndigedeCpr, list);
            assertEquals("Der skal findes 2 bemyndigelser for bemyndigende cpr " + bemyndigedeCpr, 2, list.size());
        } finally {
            ebeanServer.endTransaction();
        }
    }
}
