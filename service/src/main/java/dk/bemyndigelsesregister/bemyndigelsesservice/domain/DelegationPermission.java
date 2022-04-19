package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.*;

/**
 * BEM 2.0 rettighed til bemyndigelse
 */
@Entity
@Table(name = "bemyndigelse20_rettighed")
public class DelegationPermission extends ExternalIdentifiedDomainObject {
    @ManyToOne
    @JoinColumn(name = "bemyndigelse20_id")
    private Delegation delegation;

    @Column(name = "rettighed_kode")
    private String permissionCode;

    public DelegationPermission() {
    }

    public Delegation getDelegation() {
        return delegation;
    }

    public void setDelegation(Delegation delegation) {
        this.delegation = delegation;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }
}
