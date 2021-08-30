package bemyndigelsesservice_20170801

import org.junit.Test
import shared.WebServiceSupport

class DeleteDelegationsITCase extends WebServiceSupport {


    @Test
    public void canDeleteDelegationAsDelegator() {
        def response = send("DeleteDelegations") {
            "bms20170801:DeleteDelegationsRequest" {
                "bms20170801:DelegatorCpr"('2006271866')
                "bms20170801:ListOfDelegationIds" {
                    "bms20170801:DelegationId"('TestKode4')
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
            "bms20170801:DeleteDelegationsRequest" {
                "bms20170801:DelegateeCpr"('2006271866')
                "bms20170801:ListOfDelegationIds" {
                    "bms20170801:DelegationId"('TestKode5')
                }
            }
        }

        assert response
        assert 1 == response.DeleteDelegationsResponse.DelegationId.size()
        assert 'TestKode5' == response.DeleteDelegationsResponse.DelegationId[0].text()
    }
}
