package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Permission;

import java.util.List;

public interface PermissionDao {
    Permission get(long id);

    void save(Permission permission);

    Permission findByCode(String delegatingSystemCode, String code);

    List<Permission> findBySystem(Long delegatingSystemId);

    void remove(Permission permission);
}
