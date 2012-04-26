package sosi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.AuthenticationLevel;
import dk.sosi.seal.model.CareProvider;
import dk.sosi.seal.model.IDCard;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.SecurityTokenRequest;
import dk.sosi.seal.model.SignatureUtil;
import dk.sosi.seal.model.UserIDCard;
import dk.sosi.seal.model.UserInfo;
import dk.sosi.seal.model.constants.SubjectIdentifierTypeValues;
import dk.sosi.seal.pki.SOSIFederation;
import dk.sosi.seal.vault.ClasspathCredentialVault;
import dk.sosi.seal.xml.XmlUtil;

public class SOSIUtil {

    public static Document getIdCard() throws Exception {
        Properties props = SignatureUtil.setupCryptoProviderForJVM();

        props.setProperty(SOSIFactory.PROPERTYNAME_SOSI_VALIDATE_ENHANCED, "false");
        props.setProperty(SOSIFactory.PROPERTYNAME_SOSI_VALIDATE, "false");

        SOSIFederation federation = new SOSIFederation(props);
        ClasspathCredentialVault vault = new ClasspathCredentialVault(props, "validMocesVault.jks", "Test1234");
        SOSIFactory factory = new SOSIFactory(federation, vault, props);

        SecurityTokenRequest stRequest = factory.createNewSecurityTokenRequest();

        X509Certificate certificate = vault.getSystemCredentialPair().getCertificate();

        UserInfo userInfo = new UserInfo("2006271866", "Anita", "Testesen", "anita@trifork.com", "Doctor", "Doctor", "000");
		CareProvider careProvider = new CareProvider(SubjectIdentifierTypeValues.CVR_NUMBER, "25520041", "test");
		UserIDCard card = factory.createNewUserIDCard("SOSITEST", userInfo, careProvider, 
				AuthenticationLevel.MOCES_TRUSTED_USER, "", "", certificate, "TEST");

        stRequest.setIDCard(card);

        Document requestDocument = stRequest.serialize2DOMDocument();

        byte[] bytesForSigning = stRequest.getIDCard().getBytesForSigning(requestDocument);

        Signature jceSign = Signature.getInstance("SHA1withRSA", SignatureUtil.getCryptoProvider(props,
                SOSIFactory.PROPERTYNAME_SOSI_CRYPTOPROVIDER_SHA1WITHRSA));
        PrivateKey key = vault.getSystemCredentialPair().getPrivateKey();
        jceSign.initSign(key);
        jceSign.update(bytesForSigning);
        String signature = XmlUtil.toBase64(jceSign.sign());
        
        X509Certificate certificate2 = vault.getSystemCredentialPair().getCertificate();
        stRequest.getIDCard().injectSignature(signature, certificate2);

        String xml = XmlUtil.node2String(stRequest.serialize2DOMDocument(), false, true);
        String resXml = sendRequest("http://pan.certifikat.dk/sts/services/SecurityTokenService", "", xml, false);

        SecurityTokenRequest res = factory.deserializeSecurityTokenRequest(resXml);
        IDCard idCard = res.getIDCard();

        Request sreq = factory.createNewRequest(false, null);
        sreq.setIDCard(idCard);
        Document reqdoc = sreq.serialize2DOMDocument();

        String xmlRequest = XmlUtil.node2String(reqdoc, false, true);
        System.out.println(xmlRequest);
        return reqdoc;
    }

    private static String sendRequest(String url, String action, String docXml, boolean failOnError) throws IOException {
        URL u = new URL(url);
        HttpURLConnection uc = (HttpURLConnection) u.openConnection();
        uc.setDoOutput(true);
        uc.setDoInput(true);
        uc.setRequestMethod("POST");
        uc.setRequestProperty("SOAPAction", "\"" + action + "\"");
        uc.setRequestProperty("Content-Type", "text/xml; encoding=utf-8");
        OutputStream os = uc.getOutputStream();

        IOUtils.write(docXml, os);
        os.flush();

        InputStream is;
        if (uc.getResponseCode() != 200) {
            is = uc.getErrorStream();
        } else {
            is = uc.getInputStream();
        }
        String res = IOUtils.toString(is);

        is.close();
        if (uc.getResponseCode() != 200 && (uc.getResponseCode() != 500 || failOnError)) {
            throw new IOException(res);
        }
        os.close();
        uc.disconnect();

        return res;
    }
}
