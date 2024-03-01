package dk.bemyndigelsesregister.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Role extends ExternalIdentifiedDomainObject {
    private DelegatingSystem system;
    private String description;
    private List<String> educationCodes;
    private Set<DelegatablePermission> delegatablePermissions;

    public Role() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getEducationCodes() {
        return educationCodes;
    }

    public void setEducationCodes(List<String> educationCodes) {
        this.educationCodes = educationCodes;
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
