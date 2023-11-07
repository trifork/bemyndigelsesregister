/*
 * The MIT License
 *
 * Original work sponsored and donated by The Danish Health Data Authority (http://www.sundhedsdatastyrelsen.dk)
 *
 * Copyright (C) 2018 The Danish Health Data Authority (http://www.sundhedsdatastyrelsen.dk)
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dk.bemyndigelsesregister.ws;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.*;
import dk.sosi.seal.model.constants.SubjectIdentifierTypeValues;
import dk.sosi.seal.pki.SOSITestFederation;
import dk.sosi.seal.vault.CredentialPair;
import dk.sosi.seal.vault.CredentialVault;
import dk.sosi.seal.vault.GenericCredentialVault;
import dk.sosi.seal.xml.XmlUtil;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class SosiUtil {
    private static final String KEYSTORE_PASSWORD = "Test1234";
    private static final String CERTIFICATE_ALIAS = "sosi:alias_system";
    private static final String CERTIFICATE_CVR = "20921897";
    private static final String CERTIFICATE_CPR = "0501792275";
    private static final String CERTIFICATE_ORGANISATION = "Trifork";
    private static final String STS_URL = "http://test1-cnsp.ekstern-test.nspop.dk:8080/sts/services/NewSecurityTokenService";

    private final Properties props;
    private final CredentialVault mocesVault;
    private final CredentialVault vocesVault;

    private final SOSIFactory mocesFactory;
    private final SOSIFactory vocesFactory;

    private IDCard idCard;

    public SosiUtil() throws Exception {
        System.out.println("Initializing Properties");
        System.setProperty(SOSIFactory.PROPERTYNAME_SOSI_DO_NOT_REGISTER_STR_TRANSFORM, Boolean.TRUE.toString());
        props = SignatureUtil.setupCryptoProviderForJVM();

        System.out.println("Getting Credential Vaults");
        mocesVault = getCredentialVault("/validMocesVault.jks");
        vocesVault = getCredentialVault("/test_voces1.jks");

        System.out.println("Instantiating SOSITestFederation");
        SOSITestFederation federation = new SOSITestFederation(props);

        System.out.println("Instantiating SOSIFactories");
        mocesFactory = new SOSIFactory(federation, mocesVault, props);
        vocesFactory = new SOSIFactory(federation, vocesVault, props);
    }

    private CredentialVault getCredentialVault(String filename) throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, UnrecoverableKeyException {
        CredentialVault result = new GenericCredentialVault(props, KEYSTORE_PASSWORD);

        InputStream keyInputStream = getClass().getResourceAsStream(filename);
        result.getKeyStore().load(keyInputStream, KEYSTORE_PASSWORD.toCharArray());
        keyInputStream.close();

        X509Certificate certificate = (X509Certificate) result.getKeyStore().getCertificate(CERTIFICATE_ALIAS);
        Key priv = result.getKeyStore().getKey(CERTIFICATE_ALIAS, KEYSTORE_PASSWORD.toCharArray());
        result.setSystemCredentialPair(new CredentialPair(certificate, (PrivateKey) priv));

        return result;
    }

    /**
     * Get current IDCard if this is the first time this method is called a new IDCard is created
     *
     * @return A Valid IDCard
     * @throws IOException
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public IDCard getIdCard(SOSIFactory factory, CredentialVault vault, AuthenticationLevel authLevel) throws Exception {
        if (idCard != null && idCard.isValidInTime() && idCard.getAuthenticationLevel() == authLevel)
            return idCard;

        IDCard requestCard = null;

        if (authLevel == AuthenticationLevel.VOCES_TRUSTED_SYSTEM) {
            CareProvider careProvider = new CareProvider(SubjectIdentifierTypeValues.CVR_NUMBER, "33257872", "Sundhedsdatastyrelsen");
            requestCard = factory.createNewSystemIDCard(CERTIFICATE_ORGANISATION, careProvider, authLevel, null, null, vault.getSystemCredentialPair().getCertificate(), null);
        } else if (authLevel == AuthenticationLevel.MOCES_TRUSTED_USER) {
            CareProvider careProvider = new CareProvider(SubjectIdentifierTypeValues.CVR_NUMBER, CERTIFICATE_CVR, CERTIFICATE_ORGANISATION);
            requestCard = factory.createNewUserIDCard(CERTIFICATE_ORGANISATION, CERTIFICATE_CPR, "Lars", "Larsen", "ll@trifork.com", "developer", "Doctor", careProvider, "J0184", authLevel, vault.getSystemCredentialPair().getCertificate(), null);
        }

        if (requestCard == null) {
            throw new RuntimeException("Failed to create a new IDCard");
        }

        SecurityTokenRequest request = factory.createNewSecurityTokenRequest();
        request.setIDCard(requestCard);
        Document dom = request.serialize2DOMDocument();
        requestCard.sign(dom, vault);

        String xml = XmlUtil.node2String(request.serialize2DOMDocument(), false, true);
        String response = sendRequest(STS_URL, "\"\"", xml, true);

        SecurityTokenResponse securityTokenResponse = factory.deserializeSecurityTokenResponse(response);
        if (securityTokenResponse.isFault()) {
            System.err.println("FaultActor: " + securityTokenResponse.getFaultActor());
            System.err.println("FaultCode: " + securityTokenResponse.getFaultCode());
            System.err.println("FaultString: " + securityTokenResponse.getFaultString());
            throw new RuntimeException("Security token response is faulty: " + securityTokenResponse.getFaultString());
        }

        idCard = securityTokenResponse.getIDCard();
        if (idCard == null) {
            throw new RuntimeException("The response from the STS did not contain an IDCard:\n" + response);
        }
        return idCard;
    }

    /**
     * Sends a request to a given url
     *
     * @param url         service url
     * @param docXml      the data that should be sent
     * @param failOnError throw exception on error?
     * @return The reply from the service
     * @throws IOException
     * @throws Exception
     */
    private String sendRequest(String url, String soapAction, String docXml, boolean failOnError) throws Exception {
        HttpURLConnection uc = null;
        OutputStream os = null;
        InputStream is = null;
        try {
            URL u = new URL(url);
            uc = (HttpURLConnection) u.openConnection();
            uc.setDoOutput(true);
            uc.setDoInput(true);
            uc.setRequestMethod("POST");
            uc.setRequestProperty("SOAPAction", soapAction);
            uc.setRequestProperty("Content-Type", "text/xml; encoding=utf-8");
            os = uc.getOutputStream();
            IOUtils.write(docXml, os, StandardCharsets.UTF_8);
            os.flush();
            if (uc.getResponseCode() != 200) {
                is = uc.getErrorStream();
            } else {
                is = uc.getInputStream();
            }
            String res = IOUtils.toString(is, StandardCharsets.UTF_8);
            if (uc.getResponseCode() != 200 && (uc.getResponseCode() != 500 || failOnError)) {
                throw new Exception(res);
            }
            return res;
        } finally {
            if (os != null) IOUtils.closeQuietly(os);
            if (is != null) IOUtils.closeQuietly(is);
            if (uc != null) uc.disconnect();
        }
    }

    public void addSoapHeader(SOAPMessage soapMessage, AuthenticationLevel level) throws Exception {
        // create headers to insert in message
        SOSIFactory factory = level == AuthenticationLevel.MOCES_TRUSTED_USER ? mocesFactory : vocesFactory;
        CredentialVault vault = level == AuthenticationLevel.MOCES_TRUSTED_USER ? mocesVault : vocesVault;

        IDCard card = getIdCard(factory, vault, level);

        Request req = factory.createNewRequest(false, null);
        req.setIDCard(card);

        Document dom = req.serialize2DOMDocument();
        NodeList list = dom.getElementsByTagName("soapenv:Header");
        Node soapHeader = list.item(0);

        // insert created headers in message
        SOAPHeader header = soapMessage.getSOAPHeader();
        NodeList soapHeaderChildNodes = soapHeader.getChildNodes();
        for (int n = 0; n < soapHeaderChildNodes.getLength(); n++) {
            header.appendChild(header.getOwnerDocument().importNode(soapHeaderChildNodes.item(n), true));
        }
    }
}
