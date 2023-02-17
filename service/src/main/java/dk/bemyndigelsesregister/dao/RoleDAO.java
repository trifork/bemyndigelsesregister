package dk.bemyndigelsesregister.dao;

import dk.bemyndigelsesregister.domain.Role;

import java.util.List;

public interface RoleDAO {
    Role get(long id);

    void save(Role role);

    Role findByCode(Long systemid, String id);

    List<Role> findBySystem(Long systemId);

    void remove(long id);
}
