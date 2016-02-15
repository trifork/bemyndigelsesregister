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
        Domain domain = domainDao.findByDomainId(metadata.getDomainId());
        if (domain == null) {
            domain = new Domain();
            domain.setDomainId(metadata.getDomainId());
            domain.setSidstModificeret(now);
            domain.setSidstModificeretAf(modifiedBy);

            domainDao.save(domain);
        }

        // system
        DelegatingSystem delegatingSystem = delegatingSystemDao.findByDomainId(metadata.getSystem().getDomainId());
        if (delegatingSystem == null) {
            delegatingSystem = new DelegatingSystem();
            delegatingSystem.setDomainId(metadata.getSystem().getDomainId());
        }

        String systemDescription = metadata.getSystem().getDescription();
        if (systemDescription == null || systemDescription.trim().isEmpty())
            systemDescription = metadata.getSystem().getDomainId();

        // check if update is necessary
        if (!systemDescription.equals(delegatingSystem.getDescription()) || delegatingSystem.getDomain() == null || !metadata.getSystem().getDomainId().equals(delegatingSystem.getDomain().getDomainId())) {
            delegatingSystem.setDomain(domain);
            delegatingSystem.setDescription(systemDescription);
            delegatingSystem.setSidstModificeret(now);
            delegatingSystem.setSidstModificeretAf(modifiedBy);

            delegatingSystemDao.save(delegatingSystem);
        }

        // roles
        if (metadata.getRoles() != null) {
            List<Role> existingRoles = roleDao.findBySystem(delegatingSystem.getId());

            for (Metadata.CodeAndDescription c : metadata.getRoles()) {
                String roleDescription = c.getDescription();

                if (roleDescription == null || roleDescription.trim().isEmpty())
                    roleDescription = c.getDomainId();

                Role role = roleDao.findByDomainId(delegatingSystem.getId(), c.getDomainId());
                if (role == null) {
                    role = new Role();
                    role.setDomainId(c.getDomainId());
                    role.setDelegatingSystem(delegatingSystem);
                }

                // check if update is necessary
                if (!roleDescription.equals(role.getDescription())) {
                    role.setDescription(roleDescription);
                    role.setSidstModificeret(now);
                    role.setSidstModificeretAf(modifiedBy);

                    roleDao.save(role);
                }
            }

            if (existingRoles != null) {
                for (Role role : existingRoles) {
                    if (!metadata.containsRole(role.getDomainId()))
                        roleDao.remove(role);
                }
            }
        }

        // permissions
        if (metadata.getPermissions() != null) {
            List<Permission> existingPermissions = permissionDao.findBySystem(metadata.getSystem().getDomainId());

            for (Metadata.CodeAndDescription c : metadata.getPermissions()) {
                String permissionDescription = c.getDescription();
                if (permissionDescription == null || permissionDescription.trim().isEmpty())
                    permissionDescription = c.getDomainId();

                Permission permission = permissionDao.findByDomainId(metadata.getSystem().getDomainId(), c.getDomainId());
                if (permission == null) {
                    permission = new Permission();
                    permission.setDomainId(c.getDomainId());
                    permission.setDelegatingSystem(delegatingSystem);
                }

                // check if update is necessary
                if (!permissionDescription.equals(permission.getDescription())) {
                    permission.setDescription(permissionDescription);
                    permission.setSidstModificeret(now);
                    permission.setSidstModificeretAf(modifiedBy);

                    permissionDao.save(permission);
                }
            }

            if (existingPermissions != null) {
                for (Permission permission : existingPermissions) {
                    if (!metadata.containsPermission(permission.getDomainId()))
                        permissionDao.remove(permission);
                }
            }
        }

        // delegatable permissions
        if (metadata.getDelegatablePermissions() != null) {
            List<DelegatablePermission> existingDelegatablePermissions = delegatablePermissionDao.findBySystem(metadata.getSystem().getDomainId());

            for (Metadata.DelegatablePermission c : metadata.getDelegatablePermissions()) {
                Permission permission = permissionDao.findByDomainId(metadata.getSystem().getDomainId(), c.getPermissionId());
                if (permission == null)
                    throw new IllegalArgumentException("Cannot create delegatable permission [" + c.getPermissionId() + "] for role [" + c.getRoleId() + "]: Permission [" + c.getPermissionId() + "] not found for system [" + metadata.getSystem().getDomainId() + "]");

                Role role = roleDao.findByDomainId(delegatingSystem.getId(), c.getRoleId());
                if (role == null)
                    throw new IllegalArgumentException("Cannot create delegatable permission [" + c.getPermissionId() + "] for role [" + c.getRoleId() + "]: Role [" + c.getRoleId() + "] not found for system [" + metadata.getSystem().getDomainId() + "]");

                DelegatablePermission delegatablePermission = delegatablePermissionDao.findByPermissionAndRole(c.getPermissionId(), c.getRoleId());
                if (delegatablePermission == null) {
                    delegatablePermission = new DelegatablePermission();
                    delegatablePermission.setRole(role);
                    delegatablePermission.setPermission(permission);
                    delegatablePermission.setSidstModificeret(now);
                    delegatablePermission.setSidstModificeretAf(modifiedBy);

                    delegatablePermissionDao.save(delegatablePermission);
                }
            }

            if (existingDelegatablePermissions != null)
                for (DelegatablePermission delegatablePermission : existingDelegatablePermissions)
                    if (!metadata.containsDelegatablePermission(delegatablePermission.getRole().getDomainId(), delegatablePermission.getPermission().getDomainId()))
                        delegatablePermissionDao.remove(delegatablePermission);
        }
    }

    @Override
    public Metadata getMetadata(String domainId, String systemId) {
        Domain domain = domainDao.findByDomainId(domainId);
        if (domain == null)
            throw new IllegalArgumentException("Domain " + domainId + " not found");

        DelegatingSystem delegatingSystem = delegatingSystemDao.findByDomainId(systemId);
        if (delegatingSystem == null)
            throw new IllegalArgumentException("System " + systemId + " not found");

        Metadata metadata = new Metadata(domainId, systemId, delegatingSystem.getDescription());

        List<Role> roles = roleDao.findBySystem(delegatingSystem.getId());
        if (roles != null)
            for (Role role : roles)
                metadata.addRole(role.getDomainId(), role.getDescription());

        List<Permission> permissions = permissionDao.findBySystem(systemId);
        if (permissions != null)
            for (Permission permission : permissions)
                metadata.addPermission(permission.getDomainId(), permission.getDescription());

        List<DelegatablePermission> delegatablePermissions = delegatablePermissionDao.findBySystem(systemId);
        if (delegatablePermissions != null)
            for (DelegatablePermission delegatablePermission : delegatablePermissions)
                metadata.addDelegatablePermission(delegatablePermission.getRole().getDomainId(), delegatablePermission.getPermission().getDomainId());

        return metadata;
    }
}
