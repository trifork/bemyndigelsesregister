package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatablePermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegerbarRettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;

import java.util.List;

public interface DelegatablePermissionDao {
    DelegatablePermission get(long id);

    void save(DelegatablePermission delegatablePermission);

    List<DelegatablePermission> findBySystem(String delegatingSystemId);
}
