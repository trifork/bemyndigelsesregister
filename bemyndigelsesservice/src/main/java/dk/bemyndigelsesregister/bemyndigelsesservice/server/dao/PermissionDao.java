package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Permission;

import java.util.List;

public interface PermissionDao {
    Permission get(long id);

    void save(Permission permission);

    Permission findByDomainId(String delegatingSystemId, String domainId);

    List<Permission> findBySystem(String delegatingSystemId);
}
