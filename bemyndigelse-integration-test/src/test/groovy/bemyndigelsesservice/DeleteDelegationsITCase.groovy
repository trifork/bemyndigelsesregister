package bemyndigelsesservice

import org.junit.Test
import shared.WebServiceSupport

class DeleteDelegationsITCase extends WebServiceSupport {


    @Test
    public void canDelegationDelegationAsDelegator() {
        def response = send("CreateDelegations") {
            "bms20160101:CreateDelegationsRequest" {
                "Create" {
                    "DelegatorCpr"('2006271866')
                    "DelegateeCpr"('1010101010')
                    "DelegateeCvr"('10101010')
                    "SystemId"('testsys')
                    "RoleId"('Laege')
                    "State"('Godkendt')
                    "ListOfPermissionIds" {
                        "PermissionId"('R01')
                    }
                }
            }
        }

        assert response
        assert 1 == response.CreateDelegationsResponse.Delegation.size()
        def id = response.CreateDelegationsResponse.Delegation[0].DelegationId.text()

        response = send("DeleteDelegations") {
            "bms20160101:DeleteDelegationsRequest" {
                "ListOfDelegationIds" {
                    "DelegationId"('TestKode4')
                }
            }
        }

        assert response
        assert 1 == response.DeleteDelegationsResponse.DelegationId.size()
        assert 'TestKode4' == response.DeleteDelegationsResponse.DelegationId[0].text()
    }
}
