package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by obj on 15-02-2016.
 */
public class Metadata {
    private String domainCode;
    private CodeAndDescription system;
    private List<CodeAndDescription> roles = new LinkedList<>();
    private List<CodeAndDescription> permissions = new LinkedList<>();
    private List<DelegatablePermission> delegatablePermissions = new LinkedList<>();

    public Metadata(String domainCode, String systemCode, String systemDescription) {
        this.domainCode = domainCode;
        this.system = new CodeAndDescription(systemCode, systemDescription);
    }

    public String getDomainCode() {
        return domainCode;
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

    public void addRole(String roleCode, String roleDescription) {
        roles.add(new CodeAndDescription(roleCode, roleDescription));
    }

    public void addPermission(String permissionCode, String permissionDescription) {
        permissions.add(new CodeAndDescription(permissionCode, permissionDescription));
    }

    public void addDelegatablePermission(String roleCode, String permissionCode) {
        delegatablePermissions.add(new DelegatablePermission(roleCode, permissionCode));
    }

    public boolean containsRole(String roleCode) {
        return containsCode(roles, roleCode);
    }

    public boolean containsPermission(String permissionCode) {
        return containsCode(permissions, permissionCode);
    }

    private boolean containsCode(List<CodeAndDescription> list, String code) {
        for (CodeAndDescription c : list)
            if (c.getCode().equals(code))
                return true;
        return false;
    }

    public boolean containsDelegatablePermission(String roleCode, String permissionCode) {
        for (DelegatablePermission c : delegatablePermissions)
            if (c.getRoleCode().equals(roleCode) && c.getPermissionCode().equals(permissionCode))
                return true;
        return false;
    }

    public class CodeAndDescription {
        String code;
        String description;

        public CodeAndDescription(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    public class DelegatablePermission {
        String roleCode;
        String permissionCode;

        public DelegatablePermission(String roleCode, String permissionCode) {
            this.roleCode = roleCode;
            this.permissionCode = permissionCode;
        }

        public String getRoleCode() {
            return roleCode;
        }

        public String getPermissionCode() {
            return permissionCode;
        }
    }
}
