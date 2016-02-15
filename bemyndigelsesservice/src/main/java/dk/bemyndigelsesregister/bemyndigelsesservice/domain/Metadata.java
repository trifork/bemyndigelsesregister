package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by obj on 15-02-2016.
 */
public class Metadata {
    private String domainId;
    private CodeAndDescription system;
    private List<CodeAndDescription> roles = new LinkedList<>();
    private List<CodeAndDescription> permissions = new LinkedList<>();
    private List<DelegatablePermission> delegatablePermissions = new LinkedList<>();

    public Metadata(String domainId, String systemId, String systemDescription) {
        this.domainId = domainId;
        this.system = new CodeAndDescription(systemId, systemDescription);
    }

    public String getDomainId() {
        return domainId;
    }

    public CodeAndDescription getSystem() {
        return system;
    }

    public List<CodeAndDescription> getRoles() {
        return roles;
    }

    public List<CodeAndDescription> getPermissions() {
        return permissions;
    }

    public List<DelegatablePermission> getDelegatablePermissions() {
        return delegatablePermissions;
    }

    public void addRole(String domainId, String description) {
        roles.add(new CodeAndDescription(domainId, description));
    }

    public void addPermission(String domainId, String description) {
        permissions.add(new CodeAndDescription(domainId, description));
    }

    public void addDelegatablePermission(String roleId, String permissionId) {
        delegatablePermissions.add(new DelegatablePermission(roleId, permissionId));
    }

    public boolean containsRole(String roleId) {
        return containsDomainId(roles, roleId);
    }

    public boolean containsPermission(String permissionId) {
        return containsDomainId(permissions, permissionId);
    }

    private boolean containsDomainId(List<CodeAndDescription> list, String domainId) {
        for (CodeAndDescription c : list)
            if (c.getDomainId().equals(domainId))
                return true;
        return false;
    }

    public boolean containsDelegatablePermission(String roleId, String permissionId) {
        for (DelegatablePermission c : delegatablePermissions)
            if (c.getRoleId().equals(roleId) && c.getPermissionId().equals(permissionId))
                return true;
        return false;
    }

    public class CodeAndDescription {
        String domainId;
        String description;

        public CodeAndDescription(String domainId, String description) {
            this.domainId = domainId;
            this.description = description;
        }

        public String getDomainId() {
            return domainId;
        }

        public String getDescription() {
            return description;
        }
    }

    public class DelegatablePermission {
        String roleId;
        String permissionId;

        public DelegatablePermission(String roleId, String permissionId) {
            this.roleId = roleId;
            this.permissionId = permissionId;
        }

        public String getRoleId() {
            return roleId;
        }

        public String getPermissionId() {
            return permissionId;
        }
    }
}
