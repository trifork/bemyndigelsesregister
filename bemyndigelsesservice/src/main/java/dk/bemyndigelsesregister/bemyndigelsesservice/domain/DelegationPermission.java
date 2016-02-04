package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.*;

/**
 * BEM 2.0 rettighed til bemyndigelse
 * Created by obj on 02-02-2016.
 */
@Entity
@Table(name="bemyndigelse20_rettighed")
public class DelegationPermission extends DomainObject {
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bemyndigelse20_id")
    private Delegation delegation;

    @Column(name="rettighedKode")
    private String permissionId;

    public DelegationPermission() {
    }

    public Delegation getDelegation() {
        return delegation;
    }

    public void setDelegation(Delegation delegation) {
        this.delegation = delegation;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    @Override
    public String toString() {
        return "Bemyndigelse20Rettighed{" +
                "rettighedKode='" + permissionId + '\'' +
                '}';
    }
}
