package bemyndigelsesservice

import org.junit.Test
import wslite.soap.SOAPClient

import shared.WebServiceSupport

import static org.junit.Assert.*
import sosi.SOSIUtil
import org.w3c.dom.NodeList
import wslite.soap.SOAPFaultException

class OpretAnmodningOmBemyndigelseITCase extends WebServiceSupport {

    @Test
    public void canAccessMethod() {
        SOAPClient client = getClient()
        def response = client.send(
                SOAPAction: "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/opretAnmodningOmBemyndigelser",
        ) {
            envelopeAttributes 'xmlns:web': 'http://web.bemyndigelsesservice.bemyndigelsesregister.dk/',
                    'xmlns:sosi':"http://www.sosi.dk/sosi/2006/04/sosi-1.0.xsd"
            header {
                NodeList header = SOSIUtil.getIdCard().getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Header")
                for (int i = 0; i < header.item(0).childNodes.length; i++) {
                    String headerItem = header.item(0).childNodes.item(i) as String
                    assert headerItem
                    mkp.yieldUnescaped headerItem.substring(headerItem.indexOf("?>") + 2)
                }
            }
            body {
                "web:OpretAnmodningOmBemyndigelseRequest" {
                    "BemyndigedeCvr"(2)
                    "BemyndigedeCpr"(1)
                    "BemyndigendeCpr"(2006271866)
                    "Arbejdsfunktion"("Laege")
                    "Rettighed"("R01")
                    "System"("Trifork test system")
                }
            }
        }
        assertFalse response.hasFault()
    }

    @Test
    public void willRequireBemyndigedeCpr() {
        SOAPClient client = getClient()
        try {
            client.send(
                    SOAPAction: "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/opretAnmodningOmBemyndigelser",
            ) {
                envelopeAttributes 'xmlns:web': 'http://web.bemyndigelsesservice.bemyndigelsesregister.dk/',
                        'xmlns:sosi':"http://www.sosi.dk/sosi/2006/04/sosi-1.0.xsd"
                header {
                    NodeList header = SOSIUtil.getIdCard().getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Header")
                    for (int i = 0; i < header.item(0).childNodes.length; i++) {
                        String headerItem = header.item(0).childNodes.item(i) as String
                        assert headerItem
                        mkp.yieldUnescaped headerItem.substring(headerItem.indexOf("?>") + 2)
                    }
                }
                body {
                    "web:OpretAnmodningOmBemyndigelseRequest" {
                        "BemyndigedeCvr"(2)
                        "BemyndigendeCpr"(3)
                        "Arbejdsfunktion"("Laege")
                        "Rettighed"("R01")
                        "System"("Trifork test system")
                    }
                }
            }
            fail("No Exception was thrown")
        } catch (SOAPFaultException e) {
            assertEquals "Validation error", e.fault.':faultstring'.text()
        }
    }
}
