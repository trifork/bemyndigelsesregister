package bemyndigelsesservice_20170801

import org.junit.Test
import shared.WebServiceSupport

class CreateDelegationITCase extends WebServiceSupport {

    @Test
    public void canCreateDelegationAsDelegator() {
        def response = send("CreateDelegations") {
            "bms20170801:CreateDelegationsRequest" {
                "bms20170801:Create" {
                    "bms20170801:DelegatorCpr"('2006271866')
                    "bms20170801:DelegateeCpr"('1010101010')
                    "bms20170801:DelegateeCvr"('10101010')
                    "bms20170801:SystemId"('testsys')
                    "bms20170801:RoleId"('Laege')
                    "bms20170801:State"('Godkendt')
                    "bms20170801:ListOfPermissionIds" {
                        "bms20170801:PermissionId"('R01')
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
            "bms20170801:CreateDelegationsRequest" {
                "bms20170801:Create" {
                    "bms20170801:DelegatorCpr"('1010101010')
                    "bms20170801:DelegateeCpr"('2006271866')
                    "bms20170801:DelegateeCvr"('10101010')
                    "bms20170801:SystemId"('testsys')
                    "bms20170801:RoleId"('Laege')
                    "bms20170801:State"('Anmodet')
                    "bms20170801:ListOfPermissionIds" {
                        "bms20170801:PermissionId"('R01')
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
