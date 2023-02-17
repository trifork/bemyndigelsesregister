package dk.bemyndigelsesregister.domain;

/**
 * BEM 2.0 rettighed til bemyndigelse
 */
public class DelegationPermission extends ExternalIdentifiedDomainObject {
    private Long delegationId;
    private String permissionCode;

    public DelegationPermission() {
    }

    public Long getDelegationId() {
        return delegationId;
    }

    public void setDelegationId(Long delegationId) {
        this.delegationId = delegationId;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }
}
