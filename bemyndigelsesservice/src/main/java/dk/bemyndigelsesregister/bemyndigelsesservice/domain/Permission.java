package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "rettighed")
public class Permission extends ExternalIdentifiedDomainObject {
    @ManyToOne
    @JoinColumn(name = "linked_system_id")
    private DelegatingSystem delegatingSystem;

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

    public void setDelegatingSystem(DelegatingSystem delegatingSystem) {
        this.delegatingSystem = delegatingSystem;
    }

    public DelegatingSystem getDelegatingSystem() {
        return delegatingSystem;
    }

    public Set<DelegatablePermission> getDelegatablePermissions() {
        return delegatablePermissions;
    }

    public void setDelegatablePermissions(Set<DelegatablePermission> delegatablePermissions) {
        this.delegatablePermissions = delegatablePermissions;
    }

    public static Permission createForTest(final String domainId) {
        return new Permission() {{
            setDomainId(domainId);
        }};
    }
}
