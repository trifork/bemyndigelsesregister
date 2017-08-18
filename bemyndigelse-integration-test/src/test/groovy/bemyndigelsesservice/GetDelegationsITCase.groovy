package bemyndigelsesservice_20170801

import org.junit.Test
import shared.WebServiceSupport
import wslite.soap.SOAPFaultException

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse

class GetDelegationsITCase extends WebServiceSupport {

    @Test
    public void willNotValidateOnBothBemyndigendeAndBemyndigedeParameter() {
        try {
            send("GetDelegations") {
                "bms20170801:GetDelegationsRequest" {
                    "bms20170801:DelegatorCpr"('2006271866')
                    "bms20170801:DelegateeCpr"('1010101010')
                }
            }
        } catch (SOAPFaultException e) {
            assertEquals "Validation error", e.fault.':faultstring'.text()
        }
    }

    @Test
    public void willGetDelegationsForDelegator() {
        def response = send("GetDelegations") {
            "bms20170801:GetDelegationsRequest" {
                "bms20170801:DelegatorCpr"('2006271866')
            }

        }
        assertFalse response.hasFault()
    }

    @Test
    public void willNotValidateOnNoParameters() {
        try {
            send("GetDelegations") {
                "bms20170801:GetDelegationsRequest" {
                }
            }
        } catch (SOAPFaultException e) {
            assertEquals "Validation error", e.fault.':faultstring'.text()
        }
    }

}
