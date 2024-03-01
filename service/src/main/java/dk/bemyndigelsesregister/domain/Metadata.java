package dk.bemyndigelsesregister.domain;

import java.util.LinkedList;
import java.util.List;

public class Metadata {
    public static final String ASTERISK_PERMISSION_CODE = "*";
    public static final String ASTERISK_PERMISSION_DESCRIPTION = "Alle delegerbare rettigheder (inkl. fremtidige)";

    private final DelegatingSystem delegatingSystem;
    private final List<Role> roles = new LinkedList<>();
    private final List<Permission> permissions = new LinkedList<>();
    private final List<DelegatablePermission> delegatablePermissions = new LinkedList<>();

    public Metadata(DelegatingSystem delegatingSystem) {
        this.delegatingSystem = delegatingSystem;
    }

    public Metadata(String domainCode, String systemCode, String systemDescription) {
        Domain domain = new Domain();
        domain.setCode(domainCode);

        DelegatingSystem delegatingSystem = new DelegatingSystem();
        delegatingSystem.setCode(systemCode);
        delegatingSystem.setDescription(systemDescription);
        delegatingSystem.setDomain(domain);

        this.delegatingSystem = delegatingSystem;
    }

    public Domain getDomain() {
        return delegatingSystem.getDomain();
    }

    public DelegatingSystem getSystem() {
        return delegatingSystem;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public Role getRole(String roleCode) {
        for (Role role : roles) {
            if (role.getCode().equals(roleCode)) {
                return role;
            }
        }
        return null;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public Permission getPermission(String permissionCode) {
        for (Permission permission : permissions) {
            if (permission.getCode().equals(permissionCode)) {
                return permission;
            }
        }
        return null;
    }

    public List<DelegatablePermission> getDelegatablePermissions() {
        return delegatablePermissions;
    }

    public List<DelegatablePermission> getDelegatablePermissions(String roleCode) {
        List<DelegatablePermission> list = new LinkedList<>();
        for (DelegatablePermission p : delegatablePermissions) {
            if (p.getRole().getCode().equals(roleCode)) {
                list.add(p);
            }
        }

        return list;
    }

    public List<Permission> getUndelegatablePermissions(String roleCode) {
        List<Permission> undelegatablePermissions = new LinkedList<>();

        for (DelegatablePermission delegatablePermission : delegatablePermissions) {
            if (delegatablePermission.getRole().getCode().equals(roleCode) && !delegatablePermission.isDelegatable()) {
                for (Permission permission : permissions) {
                    if (permission.getCode().equalsIgnoreCase(delegatablePermission.getPermission().getCode())) {
                        undelegatablePermissions.add(permission);
                        break;
                    }
                }
            }
        }

        return undelegatablePermissions;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void addRole(String roleCode, String roleDescription, List<String> educationCodes) {
        Role role = new Role();
        role.setCode(roleCode);
        role.setDescription(roleDescription);
        role.setEducationCodes(educationCodes);

        addRole(role);
    }

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public void addPermission(String permissionCode, String permissionDescription) {
        Permission permission = new Permission();
        permission.setCode(permissionCode);
        permission.setDescription(permissionDescription);

        addPermission(permission);
    }

    public void addDelegatablePermission(DelegatablePermission delegatablePermission) {
        delegatablePermissions.add(delegatablePermission);
    }

    public void addDelegatablePermission(String roleCode, String permissionCode, String permissionDescription, boolean delegatable) {
        DelegatablePermission delegatablePermission = new DelegatablePermission();
        Role role = new Role();
        role.setCode(roleCode);
        delegatablePermission.setRole(role);
        Permission permission = new Permission();
        permission.setCode(permissionCode);
        permission.setDescription(permissionDescription);
        delegatablePermission.setPermission(permission);
        delegatablePermission.setDelegatable(delegatable);

        addDelegatablePermission(delegatablePermission);
    }

    public boolean containsRole(String roleCode) {
        for (Role c : roles) {
            if (c.getCode().equals(roleCode)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsPermission(String permissionCode) {
        for (Permission c : permissions) {
            if (c.getCode().equals(permissionCode)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsDelegatablePermission(String roleCode, String permissionCode) {
        for (DelegatablePermission c : delegatablePermissions) {
            if (c.getRole().getCode().equals(roleCode) && c.getPermission().getCode().equals(permissionCode)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsDelegatablePermission(String roleCode, String permissionCode, boolean delegatable) {
        for (DelegatablePermission c : delegatablePermissions)
            if (c.isDelegatable() == delegatable && c.getRole().getCode().equals(roleCode) && c.getPermission().getCode().equals(permissionCode))
                return true;
        return false;
    }
}
