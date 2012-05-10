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
                    "BemyndigendeCpr"('2006271866')
                    "BemyndigedeCpr"('1010101010')
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
                        "BemyndigendeCpr"('2006271866')
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
}
