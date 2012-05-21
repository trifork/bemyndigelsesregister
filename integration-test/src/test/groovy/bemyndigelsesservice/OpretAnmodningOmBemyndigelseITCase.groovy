package bemyndigelsesservice

import org.junit.Test
import shared.WebServiceSupport
import wslite.soap.SOAPFaultException

import static org.junit.Assert.*

class OpretAnmodningOmBemyndigelseITCase extends WebServiceSupport {

    @Test
    public void canAccessMethod() {
        def response = send("opretAnmodningOmBemyndigelser") {
            "bms:OpretAnmodningOmBemyndigelserRequest" {
                "Anmodning" {
                    "BemyndigendeCpr"('1010101010')
                    "BemyndigedeCpr"('2006271866')
                    "BemyndigedeCvr"('20000000')
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
        try {
            send("opretAnmodningOmBemyndigelser") {
                "bms:OpretAnmodningOmBemyndigelserRequest" {
                    "Anmodning" {
                        "BemyndigendeCpr"('1010101010')
                        "BemyndigedeCvr"('20000000')
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

    @Test
    public void whitelistCheckCVR() {
        SOAPClient client = getClient()
        def response = client.send(
                SOAPAction: "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/opretAnmodningOmBemyndigelser",
        ) {
            envelopeAttributes 'xmlns:web': 'http://web.bemyndigelsesservice.bemyndigelsesregister.dk/',
                    'xmlns:sosi':"http://www.sosi.dk/sosi/2006/04/sosi-1.0.xsd"
            header {
                NodeList header = SOSIUtil.getSystemIdCard().getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Header")
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

}
