package dk.bemyndigelsesregister.domain;

import java.util.HashSet;
import java.util.Set;

public class Permission extends ExternalIdentifiedDomainObject {
    private DelegatingSystem system;
    private String description;
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
        if (delegatablePermissions == null) {
            delegatablePermissions = new HashSet<>();
        }
        return delegatablePermissions;
    }

    public void setDelegatablePermissions(Set<DelegatablePermission> delegatablePermissions) {
        this.delegatablePermissions = delegatablePermissions;
    }
}
