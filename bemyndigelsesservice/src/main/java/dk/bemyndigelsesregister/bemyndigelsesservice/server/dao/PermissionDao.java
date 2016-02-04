package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;

import java.util.List;

public interface PermissionDao {
    Permission get(long id);

    void save(Permission permission);

    Permission findById(DelegatingSystem delegatingSystem, String id);

    List<Permission> findBy(DelegatingSystem delegatingSystem);
}
