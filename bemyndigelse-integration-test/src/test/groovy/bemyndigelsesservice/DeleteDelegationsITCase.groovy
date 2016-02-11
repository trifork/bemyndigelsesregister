package bemyndigelsesservice

import org.junit.Test
import shared.WebServiceSupport

class DeleteDelegationsITCase extends WebServiceSupport {


    @Test
    public void canDeleteDelegationAsDelegator() {
        def response = send("DeleteDelegations") {
            "bms20160101:DeleteDelegationsRequest" {
                "DelegatorCpr"('2006271866')
                "ListOfDelegationIds" {
                    "DelegationId"('TestKode4')
                }
            }
        }

        assert response
        assert 1 == response.DeleteDelegationsResponse.DelegationId.size()
        assert 'TestKode4' == response.DeleteDelegationsResponse.DelegationId[0].text()
    }


    @Test
    public void canDeleteDelegationAsDelegatee() {
        def response = send("DeleteDelegations") {
            "bms20160101:DeleteDelegationsRequest" {
                "DelegateeCpr"('2006271866')
                "ListOfDelegationIds" {
                    "DelegationId"('TestKode5')
                }
            }
        }

        assert response
        assert 1 == response.DeleteDelegationsResponse.DelegationId.size()
        assert 'TestKode5' == response.DeleteDelegationsResponse.DelegationId[0].text()
    }
}
