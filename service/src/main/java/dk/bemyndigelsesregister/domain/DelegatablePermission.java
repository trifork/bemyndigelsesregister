package dk.bemyndigelsesregister.domain;

public class DelegatablePermission extends DomainObject {
    private Role role;

    private Permission permission;

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
