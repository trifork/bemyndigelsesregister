package bemyndigelsesservice

import org.junit.Ignore
import org.junit.Test
import shared.WebServiceSupport10
import wslite.soap.SOAPFaultException

import static org.junit.Assert.*

class OpretAnmodningOmBemyndigelseITCase extends WebServiceSupport10 {

    @Ignore
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
                    "System"("triforktest")
                }
            }
        }
        assertFalse response.hasFault()
    }

    @Ignore
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
                        "System"("triforktest")
                    }
                }
            }
            fail("No Exception was thrown")
        } catch (SOAPFaultException e) {
            assertEquals "Validation error", e.fault.':faultstring'.text()
        }
    }
}
