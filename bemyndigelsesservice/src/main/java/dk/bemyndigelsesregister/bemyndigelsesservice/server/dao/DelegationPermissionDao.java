package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Permission;

import java.util.List;

public interface DelegationPermissionDao {
    DelegationPermission get(long id);

    void save(DelegationPermission permission);

    DelegationPermission findById(DelegatingSystem delegatingSystem, String id);

    List<DelegationPermission> findBy(DelegatingSystem delegatingSystem);
}
