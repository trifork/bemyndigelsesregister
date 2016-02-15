package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatablePermission;

import java.util.List;

public interface DelegatablePermissionDao {
    DelegatablePermission get(long id);

    void save(DelegatablePermission delegatablePermission);

    DelegatablePermission findByPermissionAndRole(String permissionId, String roleId);

    List<DelegatablePermission> findBySystem(String delegatingSystemId);

    void remove(DelegatablePermission delegatablePermission);
}
