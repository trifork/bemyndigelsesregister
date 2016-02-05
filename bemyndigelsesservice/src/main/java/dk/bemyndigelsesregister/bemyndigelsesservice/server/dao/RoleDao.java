package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Role;

import java.util.List;

public interface RoleDao {
    Role get(long id);

    void save(Role role);

    Role findByDomainId(Long systemid, String id);

    List<Role> findBySystem(Long systemId);
}
