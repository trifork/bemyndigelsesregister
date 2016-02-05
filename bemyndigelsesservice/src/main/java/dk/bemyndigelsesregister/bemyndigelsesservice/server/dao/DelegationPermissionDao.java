package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;

import java.util.List;

public interface DelegationPermissionDao {
    DelegationPermission get(long id);

    void save(DelegationPermission permission);

    List<DelegationPermission> findByDelegation(Delegation delegation);
}
