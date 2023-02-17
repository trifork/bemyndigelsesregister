package dk.bemyndigelsesregister.dao;


import dk.bemyndigelsesregister.domain.DelegationPermission;

import java.util.List;

public interface DelegationPermissionDAO {
    DelegationPermission get(long id);

    List<DelegationPermission> findByDelegationId(long delegationId);

    void save(DelegationPermission permission);

    void removeByDelegationId(long delegationId);
}
