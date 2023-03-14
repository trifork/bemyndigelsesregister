package dk.bemyndigelsesregister.ws;


import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DelegationWebServiceTest extends WebEnvironmentTest {

    private static final String GET_METADATA_SOAPACTION = "http://nsi.dk/bemyndigelse/2017/08/01#GetMetadata";
    private static final String PUT_METADATA_SOAPACTION = "http://nsi.dk/bemyndigelse/2017/08/01#PutMetadata";
    private static final String GET_ALL_METADATA_SOAPACTION = "http://nsi.dk/bemyndigelse/2017/08/01#GetAllMetadata";
    private static final String CREATE_DELEGATIONS_SOAPACTION = "http://nsi.dk/bemyndigelse/2017/08/01#CreateDelegations";
    private static final String GET_DELEGATIONS_SOAPACTION = "http://nsi.dk/bemyndigelse/2017/08/01#GetDelegations";
    private static final String DELETE_DELEGATIONS_SOAPACTION = "http://nsi.dk/bemyndigelse/2017/08/01#DeleteDelegations";
    private static final String GET_EXPIRATION_INFO_SOAPACTION = "http://nsi.dk/bemyndigelse/2017/08/01#GetExpirationInfo";

    private static final String USER_NOT_FOUND_ERROR = "Calling user not found in security context";
    private static final String SECURITY_FAULT = "Service must be called with DGWS or IDWS security";

    private static final String FMK_NAME = "Det Fælles Medicinkort";

    private static final Set<String> metadataLoaded = new HashSet<>();

    @Test
    public void canUpdateFMKMetadata() throws Exception {
        putMetadata("FMK");

        String request = loadXML("/getFMKMetadataRequest.xml");
        String response = httpWrite(GET_METADATA_SOAPACTION, request, CallMode.PLAIN);
        checkResponse("/getFMKMetadataResponse.xml", response);
    }

    @Test
    public void canUpdateDDVMetadata() throws Exception {
        putMetadata("DDV");

        String request = loadXML("/getDDVMetadataRequest.xml");
        String response = httpWrite(GET_METADATA_SOAPACTION, request, CallMode.PLAIN);
        checkResponse("/getDDVMetadataResponse.xml", response);
    }

    @Test
    public void canUpdateTASMetadata() throws Exception {
        putMetadata("TAS");

        String request = loadXML("/getTASMetadataRequest.xml");
        String response = httpWrite(GET_METADATA_SOAPACTION, request, CallMode.PLAIN);
        checkResponse("/getTASMetadataResponse.xml", response);
    }

    @Test
    public void cannotPutMetadataUsingPlainSOAP() throws Exception {
        String response = httpWrite(PUT_METADATA_SOAPACTION, loadXML("/putTASMetadataRequest.xml"), CallMode.PLAIN);
        assertTrue(response.contains(SECURITY_FAULT));
    }

    @Test
    public void canGetMetadataUsingPlainSOAP() throws Exception {
        putMetadata("TAS");

        String response = httpWrite(GET_METADATA_SOAPACTION, loadXML("/getTASMetadataRequest.xml"), CallMode.PLAIN);
        checkResponse("/getTASMetadataResponse.xml", response);
    }

    @Test
    public void canGetAllMetadata() throws Exception {
        putMetadata("FMK");
        putMetadata("DDV");
        putMetadata("TAS");

        String response = httpWrite(GET_ALL_METADATA_SOAPACTION, loadXML("/getAllMetadataRequest.xml"), CallMode.PLAIN);

        assertTrue(response.contains("Det Fælles Medicinkort"));
        assertTrue(response.contains("Det Danske Vaccinationsregister"));
        assertTrue(response.contains("Tilskudsansøgningsservicen"));
    }

    @Test
    public void canCreateDelegations() throws Exception {
        putMetadata("FMK");

        String response = httpWrite(CREATE_DELEGATIONS_SOAPACTION, loadXML("/createDelegationsRequest.xml"), CallMode.DGWS_LEVEL_4);

        System.out.println("CreateDelegationsResponse: " + response);
        assertTrue(response.contains("CreateDelegationsResponse"));
        assertTrue(response.contains(FMK_NAME));

        response = httpWrite(GET_DELEGATIONS_SOAPACTION, loadXML("/getDelegationsRequest.xml"), CallMode.DGWS_LEVEL_4);
        assertTrue(response.contains("GetDelegationsResponse"));
        assertTrue(response.contains(FMK_NAME));
    }

    @Test
    public void cannotCreateDelegationsUsingVOCES() throws Exception {
        String response = httpWrite(CREATE_DELEGATIONS_SOAPACTION, loadXML("/createDelegationsRequest.xml"), CallMode.DGWS_LEVEL_3);
        assertTrue(response.contains(USER_NOT_FOUND_ERROR));
    }

    @Test
    public void cannotCreateDelegationsUsingPlainSOAP() throws Exception {
        String response = httpWrite(CREATE_DELEGATIONS_SOAPACTION, loadXML("/createDelegationsRequest.xml"), CallMode.PLAIN);
        assertTrue(response.contains(SECURITY_FAULT));
    }

    @Test
    public void canDeleteDelegations() throws Exception {
        putMetadata("FMK");

        String response = httpWrite(CREATE_DELEGATIONS_SOAPACTION, loadXML("/createDelegationsRequest.xml"), CallMode.DGWS_LEVEL_4);

        System.out.println("CreateDelegationsResponse: " + response);
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
        response = httpWrite(DELETE_DELEGATIONS_SOAPACTION, request, CallMode.DGWS_LEVEL_4);
        assertTrue(response.contains("DeleteDelegationsResponse"));
        assertTrue(response.contains(id));
    }

    @Test
    public void cannotDeleteDelegationsUsingVOCES() throws Exception {
        String response = httpWrite(DELETE_DELEGATIONS_SOAPACTION, loadXML("/deleteDelegationsRequest.xml"), CallMode.DGWS_LEVEL_3);
        assertTrue(response.contains(USER_NOT_FOUND_ERROR));
    }

    @Test
    public void cannotDeleteDelegationsUsingPlainSOAP() throws Exception {
        String response = httpWrite(DELETE_DELEGATIONS_SOAPACTION, loadXML("/deleteDelegationsRequest.xml"), CallMode.PLAIN);
        assertTrue(response.contains(SECURITY_FAULT));
    }

    @Test
    public void canGetExpirationInfo() throws Exception {
        putMetadata("FMK");

        String response = httpWrite(CREATE_DELEGATIONS_SOAPACTION, loadXML("/createDelegationsRequest.xml"), CallMode.DGWS_LEVEL_4);

        System.out.println("CreateDelegationsResponse: " + response);
        assertTrue(response.contains("CreateDelegationsResponse"));
        assertTrue(response.contains(FMK_NAME));

        response = httpWrite(GET_EXPIRATION_INFO_SOAPACTION, loadXML("/getExpirationInfoRequest.xml"), CallMode.DGWS_LEVEL_4);
        assertTrue(response.contains("GetExpirationInfoResponse"));
    }

    @Test
    public void cannotGetExpirationInfoUsingVOCES() throws Exception {
        String response = httpWrite(GET_EXPIRATION_INFO_SOAPACTION, loadXML("/getExpirationInfoRequest.xml"), CallMode.DGWS_LEVEL_3);
        assertTrue(response.contains(USER_NOT_FOUND_ERROR));
    }

    @Test
    public void cannotGetExpirationInfoUsingPlainSOAP() throws Exception {
        String response = httpWrite(GET_EXPIRATION_INFO_SOAPACTION, loadXML("/getExpirationInfoRequest.xml"), CallMode.PLAIN);
        assertTrue(response.contains(SECURITY_FAULT));
    }

    private void putMetadata(String systemId) throws Exception {
        if (!metadataLoaded.contains(systemId)) {
            String request = loadXML("/put" + systemId + "MetadataRequest.xml");
            String response = httpWrite(PUT_METADATA_SOAPACTION, request, CallMode.DGWS_LEVEL_3);
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
