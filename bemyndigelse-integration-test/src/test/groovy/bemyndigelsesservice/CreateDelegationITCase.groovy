package bemyndigelsesservice

import org.junit.Test
import shared.WebServiceSupport

class CreateDelegationITCase extends WebServiceSupport {

    @Test
    public void canCreateDelegationAsDelegator() {
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
        assert response.CreateDelegationsResponse.Delegation[0].DelegationId.text()
        assert '2006271866' == response.CreateDelegationsResponse.Delegation[0].DelegatorCpr.text()
        assert '1010101010' == response.CreateDelegationsResponse.Delegation[0].DelegateeCpr.text()
        assert '10101010' == response.CreateDelegationsResponse.Delegation[0].DelegateeCvr.text()
        assert 'testsys' == response.CreateDelegationsResponse.Delegation[0].System.SystemId.text()
        assert 'Laege' == response.CreateDelegationsResponse.Delegation[0].Role.RoleId.text()
        assert 1 == response.CreateDelegationsResponse.Delegation[0].Permission.size()
        assert 'Godkendt' == response.CreateDelegationsResponse.Delegation[0].State.text()
    }

    @Test
    public void canCreateDelegationAsDelegatee() {
        def response = send("CreateDelegations") {
            "bms20160101:CreateDelegationsRequest" {
                "Create" {
                    "DelegatorCpr"('1010101010')
                    "DelegateeCpr"('2006271866')
                    "DelegateeCvr"('10101010')
                    "SystemId"('testsys')
                    "RoleId"('Laege')
                    "State"('Anmodet')
                    "ListOfPermissionIds" {
                        "PermissionId"('R01')
                    }
                }
            }
        }

        assert response
        assert 1 == response.CreateDelegationsResponse.Delegation.size()
        assert response.CreateDelegationsResponse.Delegation[0].DelegationId.text()
        assert '1010101010' == response.CreateDelegationsResponse.Delegation[0].DelegatorCpr.text()
        assert '2006271866' == response.CreateDelegationsResponse.Delegation[0].DelegateeCpr.text()
        assert '10101010' == response.CreateDelegationsResponse.Delegation[0].DelegateeCvr.text()
        assert 'testsys' == response.CreateDelegationsResponse.Delegation[0].System.SystemId.text()
        assert 'Laege' == response.CreateDelegationsResponse.Delegation[0].Role.RoleId.text()
        assert 1 == response.CreateDelegationsResponse.Delegation[0].Permission.size()
        assert 'Anmodet' == response.CreateDelegationsResponse.Delegation[0].State.text()
    }
}
