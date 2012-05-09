package bemyndigelsesservice

import org.junit.Test
import shared.WebServiceSupport
import wslite.soap.SOAPFaultException

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse

class HentBemyndigelserITCase extends WebServiceSupport {

    @Test
    public void willNotValidateOnBothBemyndigendeAndBemyndigedeParameter() {
        try {
            send("hentBemyndigelser") {
                "bms:HentBemyndigelserRequest" {
                    "Bemyndigende"('2006271866')
                    "Bemyndigede"('1010101010')
                }
            }
        } catch (SOAPFaultException e) {
            assertEquals "Validation error", e.fault.':faultstring'.text()
        }
    }

    @Test
    public void willValidateOnBothBemyndigendeParameter() {
        def response = send("hentBemyndigelser") {
            "bms:HentBemyndigelserRequest" {
                "Bemyndigende"('2006271866')
            }

        }
        assertFalse response.hasFault()
    }

    @Test
    public void willNotValidateOnNoParameters() {
        try {
            send("hentBemyndigelser") {
                "bms:HentBemyndigelserRequest"();
            }
        } catch (SOAPFaultException e) {
            assertEquals "Validation error", e.fault.':faultstring'.text()
        }
    }

}
