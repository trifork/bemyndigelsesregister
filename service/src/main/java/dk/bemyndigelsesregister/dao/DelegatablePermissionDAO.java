package dk.bemyndigelsesregister.dao;

import dk.bemyndigelsesregister.domain.DelegatablePermission;

import java.util.List;

public interface DelegatablePermissionDAO {
    DelegatablePermission get(long id);

    void save(DelegatablePermission delegatablePermission);

    DelegatablePermission findByPermissionAndRole(Long permissionId, Long roleId);

    List<DelegatablePermission> findByPermission(Long permissionId);

    List<DelegatablePermission> findBySystem(Long systemId);

    List<DelegatablePermission> findBySystemAndRole(Long systemId, Long roleId);

    void remove(long id);
}
