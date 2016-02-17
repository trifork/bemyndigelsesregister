package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatablePermission;

import java.util.List;

public interface DelegatablePermissionDao {
    DelegatablePermission get(long id);

    void save(DelegatablePermission delegatablePermission);

    DelegatablePermission findByPermissionAndRole(Long permissionId, Long roleId);

    List<DelegatablePermission> findBySystem(Long systemId);

    void remove(DelegatablePermission delegatablePermission);
}
