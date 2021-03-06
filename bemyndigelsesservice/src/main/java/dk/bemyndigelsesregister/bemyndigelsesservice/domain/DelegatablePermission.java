package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.*;

@Entity
@Table(name = "delegerbar_rettighed")
public class DelegatablePermission extends DomainObject {
    @ManyToOne
    @JoinColumn(name = "arbejdsfunktion_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "rettighedskode_id")
    private Permission permission;

    @Column(name = "delegerbar")
    boolean delegatable;

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isDelegatable() {
        return delegatable;
    }

    public void setDelegatable(boolean delegatable) {
        this.delegatable = delegatable;
    }
}
