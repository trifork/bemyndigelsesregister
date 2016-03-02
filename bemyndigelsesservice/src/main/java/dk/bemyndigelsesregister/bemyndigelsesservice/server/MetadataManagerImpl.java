package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by obj on 12-02-2016.
 */
@Repository
public class MetadataManagerImpl implements MetadataManager {
    @Inject
    DomainDao domainDao;

    @Inject
    DelegatingSystemDao delegatingSystemDao;

    @Inject
    RoleDao roleDao;

    @Inject
    PermissionDao permissionDao;

    @Inject
    DelegatablePermissionDao delegatablePermissionDao;

    @Override
    public void putMetadata(Metadata metadata) {
        DateTime now = DateTime.now();
        String modifiedBy = "Service";

        // domain
        Domain domain = domainDao.findByCode(metadata.getDomainCode());
        if (domain == null) {
            domain = new Domain();
            domain.setCode(metadata.getDomainCode());
            domain.setLastModified(now);
            domain.setLastModifiedBy(modifiedBy);

            domainDao.save(domain);
        }

        // system
        DelegatingSystem system = delegatingSystemDao.findByCode(metadata.getSystem().getCode());
        if (system == null) {
            system = new DelegatingSystem();
            system.setCode(metadata.getSystem().getCode());
        }

        String systemDescription = metadata.getSystem().getDescription();
        if (systemDescription == null || systemDescription.trim().isEmpty())
            systemDescription = metadata.getSystem().getCode();

        // check if update is necessary
        if (!systemDescription.equals(system.getDescription()) || system.getDomain() == null || !metadata.getSystem().getCode().equals(system.getDomain().getCode())) {
            system.setDomain(domain);
            system.setDescription(systemDescription);
            system.setLastModified(now);
            system.setLastModifiedBy(modifiedBy);

            delegatingSystemDao.save(system);
        }

        // roles
        if (metadata.getRoles() != null) {
            List<Role> existingRoles = roleDao.findBySystem(system.getId());

            for (Metadata.CodeAndDescription c : metadata.getRoles()) {
                String roleDescription = c.getDescription();

                if (roleDescription == null || roleDescription.trim().isEmpty())
                    roleDescription = c.getCode();

                Role role = roleDao.findByCode(system.getId(), c.getCode());
                if (role == null) {
                    role = new Role();
                    role.setCode(c.getCode());
                    role.setSystem(system);
                }

                // check if update is necessary
                if (!roleDescription.equals(role.getDescription())) {
                    role.setDescription(roleDescription);
                    role.setLastModified(now);
                    role.setLastModifiedBy(modifiedBy);

                    roleDao.save(role);
                }
            }

            if (existingRoles != null) {
                for (Role role : existingRoles) {
                    if (!metadata.containsRole(role.getCode()))
                        roleDao.remove(role);
                }
            }
        }

        // permissions
        if (metadata.getPermissions() != null) {
            List<Permission> existingPermissions = permissionDao.findBySystem(system.getId());

            for (Metadata.CodeAndDescription c : metadata.getPermissions()) {
                String permissionDescription = c.getDescription();
                if (permissionDescription == null || permissionDescription.trim().isEmpty())
                    permissionDescription = c.getCode();

                Permission permission = permissionDao.findByCode(metadata.getSystem().getCode(), c.getCode());
                if (permission == null) {
                    permission = new Permission();
                    permission.setCode(c.getCode());
                    permission.setSystem(system);
                }

                // check if update is necessary
                if (!permissionDescription.equals(permission.getDescription())) {
                    permission.setDescription(permissionDescription);
                    permission.setLastModified(now);
                    permission.setLastModifiedBy(modifiedBy);

                    permissionDao.save(permission);
                }
            }

            if (existingPermissions != null) {
                for (Permission permission : existingPermissions) {
                    if (!metadata.containsPermission(permission.getCode()))
                        permissionDao.remove(permission);
                }
            }
        }

        // delegatable permissions
        if (metadata.getDelegatablePermissions() != null) {
            List<DelegatablePermission> existingDelegatablePermissions = delegatablePermissionDao.findBySystem(system.getId());

            for (Metadata.DelegatablePermission c : metadata.getDelegatablePermissions()) {
                Permission permission = permissionDao.findByCode(metadata.getSystem().getCode(), c.getPermissionCode());
                if (permission == null)
                    throw new IllegalArgumentException("Cannot create delegatable permission [" + c.getPermissionCode() + "] for roleCode [" + c.getRoleCode() + "]: Permission [" + c.getPermissionCode() + "] not found for system [" + metadata.getSystem().getCode() + "]");

                Role role = roleDao.findByCode(system.getId(), c.getRoleCode());
                if (role == null)
                    throw new IllegalArgumentException("Cannot create delegatable permission [" + c.getPermissionCode() + "] for roleCode [" + c.getRoleCode() + "]: Role [" + c.getRoleCode() + "] not found for system [" + metadata.getSystem().getCode() + "]");

                DelegatablePermission delegatablePermission = delegatablePermissionDao.findByPermissionAndRole(permission.getId(), role.getId());
                if (delegatablePermission == null) {
                    delegatablePermission = new DelegatablePermission();
                    delegatablePermission.setRole(role);
                    role.getDelegatablePermissions().add(delegatablePermission);
                    delegatablePermission.setPermission(permission);
                    permission.getDelegatablePermissions().add(delegatablePermission);
                    delegatablePermission.setLastModified(now);
                    delegatablePermission.setLastModifiedBy(modifiedBy);

                    delegatablePermissionDao.save(delegatablePermission);
                }
            }

            if (existingDelegatablePermissions != null)
                for (DelegatablePermission delegatablePermission : existingDelegatablePermissions)
                    if (!metadata.containsDelegatablePermission(delegatablePermission.getRole().getCode(), delegatablePermission.getPermission().getCode()))
                        delegatablePermissionDao.remove(delegatablePermission);
        }
    }

    @Override
    public Metadata getMetadata(String domainCode, String systemCode) {
        Domain domain = domainDao.findByCode(domainCode);
        if (domain == null)
            throw new IllegalArgumentException("Domain " + domainCode + " not found");

        DelegatingSystem delegatingSystem = delegatingSystemDao.findByCode(systemCode);
        if (delegatingSystem == null)
            throw new IllegalArgumentException("System " + systemCode + " not found");

        Metadata metadata = new Metadata(domainCode, systemCode, delegatingSystem.getDescription());

        List<Role> roles = roleDao.findBySystem(delegatingSystem.getId());
        if (roles != null)
            for (Role role : roles)
                metadata.addRole(role.getCode(), role.getDescription());

        List<Permission> permissions = permissionDao.findBySystem(delegatingSystem.getId());
        if (permissions != null)
            for (Permission permission : permissions)
                metadata.addPermission(permission.getCode(), permission.getDescription());

        List<DelegatablePermission> delegatablePermissions = delegatablePermissionDao.findBySystem(delegatingSystem.getId());
        if (delegatablePermissions != null)
            for (DelegatablePermission delegatablePermission : delegatablePermissions)
                metadata.addDelegatablePermission(delegatablePermission.getRole().getCode(), delegatablePermission.getPermission().getCode());

        return metadata;
    }
}
