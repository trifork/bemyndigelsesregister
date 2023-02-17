package dk.bemyndigelsesregister.dao;

import dk.bemyndigelsesregister.domain.Permission;

import java.util.List;

public interface PermissionDAO {
    Permission get(long id);

    void save(Permission permission);

    Permission findByCode(Long delegatingSystemId, String code);

    List<Permission> findBySystem(Long delegatingSystemId);

    void remove(long id);
}
