package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "rettighed")
public class Permission extends ExternalIdentifiedDomainObject {
    @ManyToOne
    @JoinColumn(name = "linked_system_id")
    private DelegatingSystem system;

    @Column(name = "beskrivelse")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "permission")
    private Set<DelegatablePermission> delegatablePermissions;

    public Permission() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSystem(DelegatingSystem system) {
        this.system = system;
    }

    public DelegatingSystem getSystem() {
        return system;
    }

    public Set<DelegatablePermission> getDelegatablePermissions() {
        return delegatablePermissions;
    }

    public void setDelegatablePermissions(Set<DelegatablePermission> delegatablePermissions) {
        this.delegatablePermissions = delegatablePermissions;
    }
}
