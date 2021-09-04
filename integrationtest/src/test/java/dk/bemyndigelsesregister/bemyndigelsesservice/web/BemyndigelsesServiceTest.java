package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import dk.bemyndigelsesregister.bemyndigelsesservice.AbstractServiceTest;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class BemyndigelsesServiceTest extends AbstractServiceTest {

    private static final String GET_METADATA_SOAPACTION = "http://nsi.dk/bemyndigelse/2017/08/01#GetMetadata";
    private static final String PUT_METADATA_SOAPACTION = "http://nsi.dk/bemyndigelse/2017/08/01#PutMetadata";
    private static final String GET_ALL_METADATA_SOAPACTION = "http://nsi.dk/bemyndigelse/2017/08/01#GetAllMetadata";

    private static final String CREATE_DELEGATIONS_SOAPACTION = "http://nsi.dk/bemyndigelse/2017/08/01#CreateDelegations";
    private static final String GET_DELEGATIONS_SOAPACTION = "http://nsi.dk/bemyndigelse/2017/08/01#GetDelegations";
    private static final String DELETE_DELEGATIONS_SOAPACTION = "http://nsi.dk/bemyndigelse/2017/08/01#DeleteDelegations";

    private static final String GET_EXPIRATION_INFO_SOAPACTION = "http://nsi.dk/bemyndigelse/2017/08/01#GetExpirationInfo";

    private static final String FMK_NAME = "Det Fælles Medicinkort";

    private static final Set<String> metadataLoaded = new HashSet<>();

    public BemyndigelsesServiceTest() throws Exception {
    }

    @Test
    public void canUpdateFMKMetadata() throws Exception {
        putMetadata("FMK");

        String request = loadXML("/getFMKMetadataRequest.xml");
        String response = httpWrite(GET_METADATA_SOAPACTION, request, true);
        checkResponse("/getFMKMetadataResponse.xml", response);
    }


    @Test
    public void canUpdateDDVMetadata() throws Exception {
        putMetadata("DDV");

        String request = loadXML("/getDDVMetadataRequest.xml");
        String response = httpWrite(GET_METADATA_SOAPACTION, request, true);
        checkResponse("/getDDVMetadataResponse.xml", response);
    }

    @Test
    public void canUpdateTASMetadata() throws Exception {
        putMetadata("TAS");

        String request = loadXML("/getTASMetadataRequest.xml");
        String response = httpWrite(GET_METADATA_SOAPACTION, request, true);
        checkResponse("/getTASMetadataResponse.xml", response);
    }

    @Test
    public void canGetAllMetadata() throws Exception {
        putMetadata("FMK");
        putMetadata("DDV");
        putMetadata("TAS");

        String response = httpWrite(GET_ALL_METADATA_SOAPACTION, loadXML("/getAllMetadataRequest.xml"), true);

        checkResponse("/getAllMetadataResponse.xml", response);
    }

    @Test
    public void canCreateDelegations() throws Exception {
        putMetadata("FMK");

        String response = httpWrite(CREATE_DELEGATIONS_SOAPACTION, loadXML("/createDelegationsRequest.xml"), true);

        System.out.println("GetMetadata response: " + response);
        assertTrue(response.contains("CreateDelegationsResponse"));
        assertTrue(response.contains(FMK_NAME));

        response = httpWrite(GET_DELEGATIONS_SOAPACTION, loadXML("/getDelegationsRequest.xml"), true);
        assertTrue(response.contains("GetDelegationsResponse"));
        assertTrue(response.contains(FMK_NAME));
    }

    @Test
    public void canDeleteDelegations() throws Exception {
        putMetadata("FMK");

        String response = httpWrite(CREATE_DELEGATIONS_SOAPACTION, loadXML("/createDelegationsRequest.xml"), true);

        System.out.println("GetMetadata response: " + response);
        assertTrue(response.contains("CreateDelegationsResponse"));
        assertTrue(response.contains(FMK_NAME));

        // grab an id
        String id = null;
        int startPos = response.indexOf("DelegationId>");
        if (startPos > 0) {
            int endPos = response.indexOf("<", startPos);
            if (endPos > 0) {
                id = response.substring(startPos + 13, endPos);
            }
        }
        assertNotNull(id);

        String request = loadXML("/deleteDelegationsRequest.xml");
        request = request.replaceAll("DELEGATION_ID", id);
        response = httpWrite(DELETE_DELEGATIONS_SOAPACTION, request, true);
        assertTrue(response.contains("DeleteDelegationsResponse"));
        assertTrue(response.contains(id));
    }

    @Test
    public void canGetExpirationInfo() throws Exception {
        putMetadata("FMK");

        String response = httpWrite(CREATE_DELEGATIONS_SOAPACTION, loadXML("/createDelegationsRequest.xml"), true);

        System.out.println("GetMetadata response: " + response);
        assertTrue(response.contains("CreateDelegationsResponse"));
        assertTrue(response.contains(FMK_NAME));

        response = httpWrite(GET_EXPIRATION_INFO_SOAPACTION, loadXML("/getExpirationInfoRequest.xml"), true);
        assertTrue(response.contains("GetExpirationInfoResponse"));
    }

    private void putMetadata(String systemId) throws Exception {
        if (!metadataLoaded.contains(systemId)) {
            String request = loadXML("/put" + systemId + "MetadataRequest.xml");
            String response = httpWrite(PUT_METADATA_SOAPACTION, request, true);
            checkResponse("/put" + systemId + "MetadataResponse.xml", response);

            metadataLoaded.add(systemId);
        }
    }

    private void checkResponse(String filename, String response) throws Exception {
        assertEquals(stripWhitespaceEtc(loadXML(filename)), stripWhitespaceEtc(response));
    }

    private String stripWhitespaceEtc(String xml) throws Exception {
        return xml.replaceAll("\r\n|[ ]", "");
    }
}
