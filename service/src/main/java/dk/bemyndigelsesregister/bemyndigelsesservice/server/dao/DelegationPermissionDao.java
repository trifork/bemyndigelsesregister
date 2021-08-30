package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;

public interface DelegationPermissionDao {
    DelegationPermission get(long id);

    void save(DelegationPermission permission);

    void remove(DelegationPermission delegationPermission);
}
