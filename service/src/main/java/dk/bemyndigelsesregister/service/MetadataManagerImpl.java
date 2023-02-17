package dk.bemyndigelsesregister.service;

import dk.bemyndigelsesregister.dao.*;
import dk.bemyndigelsesregister.domain.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Repository
public class MetadataManagerImpl implements MetadataManager {
    private static final Logger logger = LogManager.getLogger(MetadataManagerImpl.class);

    @Autowired
    private DomainDAO domainDao;

    @Autowired
    private DelegatingSystemDAO delegatingSystemDao;

    @Autowired
    private RoleDAO roleDao;

    @Autowired
    private PermissionDAO permissionDao;

    @Autowired
    private DelegatablePermissionDAO delegatablePermissionDao;

    @Autowired
    private MetadataCache metadataCache;

    @Autowired
    private SystemVariableDAO systemVariableDAO;

    @PostConstruct
    private void init() {
        logger.info("DelegationManager initialised");
    }

    @Override
    @Transactional
    public void putMetadata(Metadata metadata) {
        logger.info("putMetadata started, domain=[" + metadata.getDomain().getCode() + "], system=[" + metadata.getSystem().getCode() + "]");

        // domain
        Domain domain = domainDao.findByCode(metadata.getDomain().getCode());
        if (domain == null) {
            domain = new Domain();
            domain.setCode(metadata.getDomain().getCode());

            domainDao.save(domain);
            logger.info("  Domain [" + metadata.getDomain().getCode() + "] created");
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
        if (!systemDescription.equals(system.getDescription()) || system.getDomain() == null || !metadata.getDomain().getCode().equals(system.getDomain().getCode())) {
            system.setDomain(domain);
            system.setDescription(systemDescription);

            delegatingSystemDao.save(system);

            systemUpdated = true;
            logger.info("  System [" + metadata.getSystem() + "] updated, description=[" + systemDescription + "]");
        }

        boolean delegatablePermissionChange = false;

        // remove obsolete delegatable permissions (before removing obsolete roles)
        if (metadata.getDelegatablePermissions() != null) {
            List<DelegatablePermission> existingDelegatablePermissions = delegatablePermissionDao.findBySystem(system.getId());
            if (existingDelegatablePermissions != null) {
                for (DelegatablePermission delegatablePermission : existingDelegatablePermissions) {
                    if (!metadata.containsDelegatablePermission(delegatablePermission.getRole().getCode(), delegatablePermission.getPermission().getCode())) {
                        logger.info("  DelegatablePermission [" + delegatablePermission.getRole().getCode() + "]:[" + delegatablePermission.getPermission().getCode() + "] removed");

                        delegatablePermissionDao.remove(delegatablePermission.getId());
                        delegatablePermissionChange = true;
                    }
                }
            }
        }

        // roles
        if (metadata.getRoles() != null) {
            List<Role> existingRoles = roleDao.findBySystem(system.getId());

            for (Role c : metadata.getRoles()) {
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

                    roleDao.save(role);

                    logger.info("  Role [" + c + "] updated, description=[" + roleDescription + "]");
                }
            }

            if (existingRoles != null) {
                for (Role role : existingRoles) {
                    if (!metadata.containsRole(role.getCode())) {
                        logger.info("  Role [" + role.getCode() + "] removed");

                        roleDao.remove(role.getId());
                    }
                }
            }
        }

        // permissions
        if (metadata.getPermissions() != null) {
            List<Permission> existingPermissions = permissionDao.findBySystem(system.getId());

            for (Permission c : metadata.getPermissions()) {
                if (c.getCode().contains(Metadata.ASTERISK_PERMISSION_CODE) && !c.getCode().equals(Metadata.ASTERISK_PERMISSION_CODE))
                    throw new IllegalArgumentException("Illegal permission [" + c.getCode() + "]. Code must either be equal to \"" + Metadata.ASTERISK_PERMISSION_CODE + "\" or not contain \"" + Metadata.ASTERISK_PERMISSION_CODE + "\"-characters");

                String permissionDescription = c.getDescription();
                if (permissionDescription == null || permissionDescription.trim().isEmpty())
                    permissionDescription = c.getCode();

                Permission permission = permissionDao.findByCode(system.getId(), c.getCode());
                if (permission == null) {
                    permission = new Permission();
                    permission.setCode(c.getCode());
                    permission.setSystem(system);

                    logger.info("  Permission [" + c + "] created");
                }

                // check if update is necessary
                if (!permissionDescription.equals(permission.getDescription())) {
                    permission.setDescription(permissionDescription);

                    permissionDao.save(permission);

                    logger.info("  Permission [" + c + "] updated, description=[" + permissionDescription + "]");
                }
            }

            if (existingPermissions != null) {
                for (Permission permission : existingPermissions) {
                    if (!metadata.containsPermission(permission.getCode())) {
                        logger.info("  Permission [" + permission.getCode() + "] removed");

                        permissionDao.remove(permission.getId());
                    }
                }
            }
        }

        // create/update delegatable permissions
        if (metadata.getDelegatablePermissions() != null) {
            List<DelegatablePermission> existingDelegatablePermissions = delegatablePermissionDao.findBySystem(system.getId());

            for (DelegatablePermission c : metadata.getDelegatablePermissions()) {
                Permission permission = permissionDao.findByCode(system.getId(), c.getPermission().getCode());
                if (permission == null)
                    throw new IllegalArgumentException("Cannot create delegatable permission [" + c.getPermission().getCode() + "] for role [" + c.getRole().getCode() + "]: Permission [" + c.getPermission().getCode() + "] not found for system [" + metadata.getSystem().getCode() + "]");

                Role role = roleDao.findByCode(system.getId(), c.getRole().getCode());
                if (role == null)
                    throw new IllegalArgumentException("Cannot create delegatable permission [" + c.getPermission().getCode() + "] for role [" + c.getRole().getCode() + "]: Role [" + c.getRole().getCode() + "] not found for system [" + metadata.getSystem().getCode() + "]");

                DelegatablePermission delegatablePermission = delegatablePermissionDao.findByPermissionAndRole(permission.getId(), role.getId());
                if (delegatablePermission == null) {
                    delegatablePermission = new DelegatablePermission();
                    delegatablePermission.setRole(role);
                    role.getDelegatablePermissions().add(delegatablePermission);
                    delegatablePermission.setPermission(permission);
                    permission.getDelegatablePermissions().add(delegatablePermission);
                    delegatablePermission.setDelegatable(c.isDelegatable());

                    delegatablePermissionDao.save(delegatablePermission);

                    delegatablePermissionChange = true;
                    logger.info("  DelegatablePermission [" + c.getRole().getCode() + "]:[" + c.getPermission().getCode() + "] added, delegatable=[" + c.isDelegatable() + "]");
                } else {
                    if (delegatablePermission.isDelegatable() != c.isDelegatable()) {
                        delegatablePermission.setDelegatable(c.isDelegatable());
                        delegatablePermissionDao.save(delegatablePermission);

                        delegatablePermissionChange = true;
                        logger.info("  DelegatablePermission [" + c.getRole().getCode() + "]:[" + c.getPermission().getCode() + "] updated to delegatable=[" + c.isDelegatable() + "]");
                    }
                }
            }
        }

        if (delegatablePermissionChange && !systemUpdated) { // update system timestamp to signal change in delegatable permissions to asterisk expander
            delegatingSystemDao.save(system);

            logger.info("  System [" + metadata.getSystem() + "] marked as modified");
        }

        metadataCache.clear(domain.getCode(), system.getCode());

        // Let other servers know that metadata was updated
        SystemVariable lastUpdate = systemVariableDAO.getByName(MetadataManager.LAST_METADATA_UPDATE_SYSTEM_VARIABLE);
        if (lastUpdate == null) {
            lastUpdate = new SystemVariable(MetadataManager.LAST_METADATA_UPDATE_SYSTEM_VARIABLE, Instant.now());
        } else {
            lastUpdate.setInstantValue(Instant.now());
        }
        systemVariableDAO.save(lastUpdate);

        logger.info("putMetadata ended");
    }

    @Override
    public Metadata getMetadata(String domainCode, String systemCode) {
        Metadata metadata = metadataCache.get(domainCode, systemCode);
        if (metadata != null) {
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

        metadata = getMetadata(delegatingSystem);
        metadataCache.put(metadata);

        return metadata;
    }

    @Override
    public List<Metadata> getAllMetadata(String domainCode) {
        List<Metadata> result = new LinkedList<>();

        Domain domain = domainDao.findByCode(domainCode);
        if (domain == null)
            throw new IllegalArgumentException("Domain [" + domainCode + "] not found");

        List<DelegatingSystem> systems = delegatingSystemDao.findByDomain(domain.getId());
        if (systems != null) {
            for (DelegatingSystem delegatingSystem : systems) {
                Metadata metadata = metadataCache.get(domainCode, delegatingSystem.getCode());
                if (metadata == null) {
                    metadata = getMetadata(delegatingSystem);
                    metadataCache.put(metadata);
                }
                result.add(metadata);
            }
        }

        return result;
    }

    private Metadata getMetadata(DelegatingSystem delegatingSystem) {
        Metadata metadata = new Metadata(delegatingSystem);

        List<Role> roles = roleDao.findBySystem(delegatingSystem.getId());
        if (roles != null) {
            for (Role role : roles) {
                role.setSystem(delegatingSystem);
                role.setDelegatablePermissions(new HashSet<>(delegatablePermissionDao.findBySystemAndRole(delegatingSystem.getId(), role.getId())));
                metadata.addRole(role.getCode(), role.getDescription());
            }
        }

        List<Permission> permissions = permissionDao.findBySystem(delegatingSystem.getId());
        if (permissions != null) {
            for (Permission permission : permissions) {
                permission.setSystem(delegatingSystem);
                metadata.addPermission(permission.getCode(), permission.getDescription());
            }
        }

        List<DelegatablePermission> delegatablePermissions = delegatablePermissionDao.findBySystem(delegatingSystem.getId());
        if (delegatablePermissions != null)
            for (DelegatablePermission delegatablePermission : delegatablePermissions)
                metadata.addDelegatablePermission(delegatablePermission.getRole().getCode(), delegatablePermission.getPermission().getCode(), delegatablePermission.getPermission().getDescription(), delegatablePermission.isDelegatable());

        logger.info("Metadata loaded from database, system=[" + delegatingSystem.getCode() + "]");

        return metadata;
    }
}
