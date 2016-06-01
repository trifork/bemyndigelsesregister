package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.util.EhCache;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by obj on 12-02-2016.
 */
@Repository
public class MetadataManagerImpl implements MetadataManager {
    private static Logger logger = Logger.getLogger(MetadataManagerImpl.class);

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

    private static EhCache<String, Metadata> metadataCache = new EhCache<>("MetadataCache");

    @Override
    public void putMetadata(Metadata metadata) {
        logger.info("putMetadata started, domain=[" + metadata.getDomainCode() + "], system=[" + metadata.getSystem().getCode() + "]");
        DateTime now = DateTime.now();

        // domain
        Domain domain = domainDao.findByCode(metadata.getDomainCode());
        if (domain == null) {
            domain = new Domain();
            domain.setCode(metadata.getDomainCode());
            domain.setLastModified(now);
            domain.setLastModifiedBy(getClass().getSimpleName());

            domainDao.save(domain);
            logger.info("  Domain [" + metadata.getDomainCode() + "] created");
        }

        boolean systemUpdated = false;

        // system
        DelegatingSystem system = delegatingSystemDao.findByCode(metadata.getSystem().getCode());
        if (system == null) {
            system = new DelegatingSystem();
            system.setCode(metadata.getSystem().getCode());
            logger.info("  System [" + metadata.getSystem() + "] created");
        }

        String systemDescription = metadata.getSystem().getDescription();
        if (systemDescription == null || systemDescription.trim().isEmpty())
            systemDescription = metadata.getSystem().getCode();

        // check if update is necessary
        if (!systemDescription.equals(system.getDescription()) || system.getDomain() == null || !metadata.getDomainCode().equals(system.getDomain().getCode())) {
            system.setDomain(domain);
            system.setDescription(systemDescription);
            system.setLastModified(now);
            system.setLastModifiedBy(getClass().getSimpleName());

            delegatingSystemDao.save(system);

            systemUpdated = true;
            logger.info("  System [" + metadata.getSystem() + "] updated, description=[" + systemDescription + "]");
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

                    logger.info("  Role [" + c + "] created");
                }

                // check if update is necessary
                if (!roleDescription.equals(role.getDescription())) {
                    role.setDescription(roleDescription);
                    role.setLastModified(now);
                    role.setLastModifiedBy(getClass().getSimpleName());

                    roleDao.save(role);

                    logger.info("  Role [" + c + "] updated, description=[" + roleDescription + "]");
                }
            }

            if (existingRoles != null) {
                for (Role role : existingRoles) {
                    if (!metadata.containsRole(role.getCode())) {
                        logger.info("  Role [" + role.getCode() + "] removed");

                        roleDao.remove(role);
                    }
                }
            }
        }

        // permissions
        if (metadata.getPermissions() != null) {
            List<Permission> existingPermissions = permissionDao.findBySystem(system.getId());

            for (Metadata.CodeAndDescription c : metadata.getPermissions()) {
                if (c.getCode().contains(Metadata.ASTERISK_PERMISSION_CODE) && !c.getCode().equals(Metadata.ASTERISK_PERMISSION_CODE))
                    throw new IllegalArgumentException("Illegal permission [" + c.getCode() + "]. Code must either be equal to \"" + Metadata.ASTERISK_PERMISSION_CODE + "\" or not contain \"" + Metadata.ASTERISK_PERMISSION_CODE + "\"-characters");

                String permissionDescription = c.getDescription();
                if (permissionDescription == null || permissionDescription.trim().isEmpty())
                    permissionDescription = c.getCode();

                Permission permission = permissionDao.findByCode(metadata.getSystem().getCode(), c.getCode());
                if (permission == null) {
                    permission = new Permission();
                    permission.setCode(c.getCode());
                    permission.setSystem(system);

                    logger.info("  Permission [" + c + "] created");
                }

                // check if update is necessary
                if (!permissionDescription.equals(permission.getDescription())) {
                    permission.setDescription(permissionDescription);
                    permission.setLastModified(now);
                    permission.setLastModifiedBy(getClass().getSimpleName());

                    permissionDao.save(permission);

                    logger.info("  Permission [" + c + "] updated, description=[" + permissionDescription + "]");
                }
            }

            if (existingPermissions != null) {
                for (Permission permission : existingPermissions) {
                    if (!metadata.containsPermission(permission.getCode())) {
                        logger.info("  Permission [" + permission.getCode() + "] removed");

                        permissionDao.remove(permission);
                    }
                }
            }
        }

        boolean delegatablePermissionChange = false;

        // delegatable permissions
        if (metadata.getDelegatablePermissions() != null) {
            List<DelegatablePermission> existingDelegatablePermissions = delegatablePermissionDao.findBySystem(system.getId());

            for (Metadata.DelegatablePermission c : metadata.getDelegatablePermissions()) {
                Permission permission = permissionDao.findByCode(metadata.getSystem().getCode(), c.getPermissionCode());
                if (permission == null)
                    throw new IllegalArgumentException("Cannot create delegatable permission [" + c.getPermissionCode() + "] for role [" + c.getRoleCode() + "]: Permission [" + c.getPermissionCode() + "] not found for system [" + metadata.getSystem().getCode() + "]");

                Role role = roleDao.findByCode(system.getId(), c.getRoleCode());
                if (role == null)
                    throw new IllegalArgumentException("Cannot create delegatable permission [" + c.getPermissionCode() + "] for role [" + c.getRoleCode() + "]: Role [" + c.getRoleCode() + "] not found for system [" + metadata.getSystem().getCode() + "]");

                DelegatablePermission delegatablePermission = delegatablePermissionDao.findByPermissionAndRole(permission.getId(), role.getId());
                if (delegatablePermission == null) {
                    delegatablePermission = new DelegatablePermission();
                    delegatablePermission.setRole(role);
                    role.getDelegatablePermissions().add(delegatablePermission);
                    delegatablePermission.setPermission(permission);
                    permission.getDelegatablePermissions().add(delegatablePermission);
                    delegatablePermission.setDelegatable(c.isDelegatable());
                    delegatablePermission.setLastModified(now);
                    delegatablePermission.setLastModifiedBy(getClass().getSimpleName());

                    delegatablePermissionDao.save(delegatablePermission);

                    delegatablePermissionChange = true;
                    logger.info("  DelegatablePermission [" + c.getRoleCode() + "]:[" + c.getPermissionCode() + "] added, delegatable=[" + c.isDelegatable() + "]");
                } else {
                    if (delegatablePermission.isDelegatable() != c.isDelegatable()) {
                        delegatablePermission.setDelegatable(c.isDelegatable());
                        delegatablePermissionDao.save(delegatablePermission);

                        delegatablePermissionChange = true;
                        logger.info("  DelegatablePermission [" + c.getRoleCode() + "]:[" + c.getPermissionCode() + "] updated to delegatable=[" + c.isDelegatable() + "]");
                    }
                }
            }

            if (existingDelegatablePermissions != null) {
                for (DelegatablePermission delegatablePermission : existingDelegatablePermissions) {
                    if (!metadata.containsDelegatablePermission(delegatablePermission.getRole().getCode(), delegatablePermission.getPermission().getCode())) {
                        logger.info("  DelegatablePermission [" + delegatablePermission.getRole().getCode() + "]:[" + delegatablePermission.getPermission().getCode() + "] removed");

                        delegatablePermissionDao.remove(delegatablePermission);
                        delegatablePermissionChange = true;
                    }
                }
            }
        }

        if (delegatablePermissionChange && !systemUpdated) { // update system timestamp to signal change in delegatable permissions to asterisk expander
            system.setLastModified(now);
            system.setLastModifiedBy(getClass().getSimpleName());

            delegatingSystemDao.save(system);

            logger.info("  System [" + metadata.getSystem() + "] marked as modified");
        }

        metadataCache.clear();
        logger.info("putMetadata ended");
    }

    @Override
    public Metadata getMetadata(String domainCode, String systemCode) {
        Metadata metadata = metadataCache.get(systemCode);
        if (metadata != null) {
            logger.info("Metadata found in cache, system=[" + systemCode + "]");
            return metadata;
        }

        if (domainCode != null) {
            Domain domain = domainDao.findByCode(domainCode);
            if (domain == null)
                throw new IllegalArgumentException("Domain [" + domainCode + "] not found");
        }

        DelegatingSystem delegatingSystem = delegatingSystemDao.findByCode(systemCode);
        if (delegatingSystem == null)
            throw new IllegalArgumentException("System [" + systemCode + "] not found");

        metadata = new Metadata(delegatingSystem.getDomain().getCode(), delegatingSystem.getCode(), delegatingSystem.getDescription());

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
                metadata.addDelegatablePermission(delegatablePermission.getRole().getCode(), delegatablePermission.getPermission().getCode(), delegatablePermission.getPermission().getDescription(), delegatablePermission.isDelegatable());

        metadataCache.put(systemCode, metadata);

        return metadata;
    }
}
