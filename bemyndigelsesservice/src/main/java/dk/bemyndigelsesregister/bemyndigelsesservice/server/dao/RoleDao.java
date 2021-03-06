package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Role;

import java.util.List;

public interface RoleDao {
    Role get(long id);

    void save(Role role);

    Role findByCode(Long systemid, String id);

    List<Role> findBySystem(Long systemId);

    void remove(Role role);
}
