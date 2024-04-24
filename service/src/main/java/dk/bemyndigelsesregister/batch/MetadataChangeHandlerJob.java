package dk.bemyndigelsesregister.batch;

import dk.bemyndigelsesregister.dao.DelegatingSystemDAO;
import dk.bemyndigelsesregister.dao.DelegationDAO;
import dk.bemyndigelsesregister.dao.SystemVariableDAO;
import dk.bemyndigelsesregister.domain.*;
import dk.bemyndigelsesregister.service.DelegationManager;
import dk.bemyndigelsesregister.service.MetadataCache;
import dk.bemyndigelsesregister.service.MetadataManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

/**
 * Job to update permissions in delegations with * permission
 */

@Component
public class MetadataChangeHandlerJob extends AbstractJob {
    private static final Logger logger = LogManager.getLogger(MetadataChangeHandlerJob.class);
    private static final String LAST_RUN_SYSTEM_VARIABLE = "lastMetadataChangeHandlerRun";

    private Map<String, Map<String, Set<String>>> systemRolePermissionMap; // systemcode, rolecode, set of permissioncodes

    @Autowired
    private DelegatingSystemDAO delegatingSystemDAO;

    @Autowired
    private DelegationDAO delegationDAO;

    @Autowired
    private SystemVariableDAO systemVariableDAO;

    @Autowired
    private MetadataManager metadataManager;

    @Autowired
    private DelegationManager delegationManager;

    @Autowired
    private MetadataCache metadataCache;

    @Value("${metadatachangehandlerjob.enabled}")
    private String jobEnabled;

    public MetadataChangeHandlerJob() {
        super(logger, "MetadataChangeHandler");
    }

    @Scheduled(cron = "${metadatachangehandlerjob.cron}")
    public void start() {
        try {
            initJob();

            if (Boolean.parseBoolean(jobEnabled)) {
                startJob();

                SystemVariable lastRun = systemVariableDAO.getByName(LAST_RUN_SYSTEM_VARIABLE);
                if (lastRun == null) {
                    lastRun = new SystemVariable(LAST_RUN_SYSTEM_VARIABLE, Instant.now());
                }
                Instant startTime = Instant.now();

                // update delegations with asterisk permission for changed systems
                handleChangedMetadata(startTime, lastRun.getInstantValue());

                updateLastRun(lastRun, startTime);
                endJob();
            } else {
                jobDisabled();
            }
        } catch (Exception ex) {
            logger.error("An error occurred during reset of metadata cache", ex);
        } finally {
            cleanupJob();
        }
    }


    public void handleChangedMetadata(Instant startTime, Instant lastRun) {
        systemRolePermissionMap = null;

        List<DelegatingSystem> systems = delegatingSystemDAO.findByLastModifiedGreaterThanOrEquals(lastRun);

        if (systems == null || systems.isEmpty()) {
            logger.info("No metadata changed");
        } else {
            for (DelegatingSystem system : systems) {
                metadataCache.clear(Domain.DEFAULT_DOMAIN, system.getCode());

                String hashSystemVariableName = system.getCode() + "Hash";
                String newHash = getRolePermissionHash(system.getCode());

                SystemVariable hashSystemVariable = systemVariableDAO.getByName(hashSystemVariableName);
                String oldHash = hashSystemVariable != null ? hashSystemVariable.getValue() : "";

                if (newHash.equals(oldHash)) {
                    logger.info("Role/permission metadata hash [" + newHash + "] unchanged for system [" + system.getCode() + "], skipping delegation updates");
                } else {
                    updateDelegationsWithAsteriskForSystem(startTime, system.getCode());

                    // save hash
                    if (hashSystemVariable == null) {
                        hashSystemVariable = new SystemVariable(hashSystemVariableName, newHash);
                    } else {
                        hashSystemVariable.setValue(newHash);
                    }
                    systemVariableDAO.save(hashSystemVariable);

                    logger.info("Saved new role/permission metadata hash [" + newHash + "] for system [" + system.getCode() + "]");
                }
            }
        }

        systemRolePermissionMap = null;
    }

    private void updateDelegationsWithAsteriskForSystem(Instant startTime, String systemCode) {
        logger.info("Processing system [" + systemCode + "]. *-permissions to delegate: ");

        Map<String, Set<String>> rolePermissionMap = getRolePermissionMap(systemCode);
        for (String roleCode : rolePermissionMap.keySet()) {
            logger.info("   [" + roleCode + "]: " + rolePermissionMap.get(roleCode));
        }

        List<Long> delegationIds = delegationDAO.findWithAsterisk(systemCode, startTime);
        updatePermissionsForDelegations(startTime, systemCode, delegationIds);
    }

    private void updatePermissionsForDelegations(Instant startTime, String systemCode, List<Long> delegationIds) {
        Metadata metadata = metadataManager.getMetadata(Domain.DEFAULT_DOMAIN, systemCode);
        if (metadata != null && metadata.getDelegatablePermissions() != null) {
            logger.info("Start updating permissions for " + delegationIds.size() + " delegations with *-permission");
            int updateCount = 0;
            for (Long delegationId : delegationIds) {
                Delegation delegation = delegationDAO.get(delegationId);
                if (delegation.getState() == Status.GODKENDT) {
                    boolean update = true;

                    if (delegation.getDelegationPermissions() != null) {
                        // check if permissions is unchanged

                        Set<String> permissionCodesDelegation = new HashSet<>();
                        for (DelegationPermission dp : delegation.getDelegationPermissions()) {
                            if (!Metadata.ASTERISK_PERMISSION_CODE.equals(dp.getPermissionCode())) {
                                permissionCodesDelegation.add(dp.getPermissionCode());
                            }
                        }

                        Set<String> permissionCodesMetadata = new HashSet<>();
                        for (DelegatablePermission dp : metadata.getDelegatablePermissions(delegation.getRoleCode())) {
                            if (dp.isDelegatable() && !Metadata.ASTERISK_PERMISSION_CODE.equals(dp.getPermission().getCode())) {
                                permissionCodesMetadata.add(dp.getPermission().getCode());
                            }
                        }

                        if (permissionCodesDelegation.equals(permissionCodesMetadata)) {
                            update = false;
                        }
                    }

                    if (update) {
                        // delegationManager will expand asterisk to new permissions
                        delegationManager.createDelegation(delegation.getSystemCode(), delegation.getDelegatorCpr(), delegation.getDelegateeCpr(), delegation.getDelegateeCvr(), delegation.getRoleCode(), Status.GODKENDT, Collections.singletonList(Metadata.ASTERISK_PERMISSION_CODE), startTime, delegation.getEffectiveTo());
                        updateCount++;
                    }
                }
            }
            logger.info("End updating permissions, updated " + updateCount + " of " + delegationIds.size() + " delegations");
        }
    }

    /**
     * Returns a hash value for all role/permissions for a specific system
     */
    private String getRolePermissionHash(String systemCode) {
        Map<String, Set<String>> rolePermissionMap = getRolePermissionMap(systemCode);
        StringBuilder buf = new StringBuilder();
        for (String role : rolePermissionMap.keySet()) {
            buf.append(role).append(":\n");
            for (String permission : rolePermissionMap.get(role)) {
                buf.append(" ").append(permission).append("\n");
            }
        }

        return String.valueOf(buf.toString().hashCode());
    }

    private Map<String, Set<String>> getRolePermissionMap(String systemCode) {
        if (systemRolePermissionMap == null) {
            systemRolePermissionMap = new HashMap<>();
        }

        Map<String, Set<String>> rolePermissionMap = systemRolePermissionMap.get(systemCode);
        if (rolePermissionMap == null) {
            rolePermissionMap = new HashMap<>();
            systemRolePermissionMap.put(systemCode, rolePermissionMap);

            Metadata metadata = metadataManager.getMetadata(Domain.DEFAULT_DOMAIN, systemCode);
            if (metadata != null && metadata.getDelegatablePermissions() != null) {
                for (DelegatablePermission dp : metadata.getDelegatablePermissions()) {
                    if (dp.isDelegatable() && !dp.getPermission().getCode().equals(Metadata.ASTERISK_PERMISSION_CODE)) {
                        Set<String> permissionCodes = rolePermissionMap.get(dp.getRole().getCode());
                        if (permissionCodes == null) {
                            permissionCodes = new HashSet<>();
                            rolePermissionMap.put(dp.getRole().getCode(), permissionCodes);
                        }
                        permissionCodes.add(dp.getPermission().getCode());
                    }
                }
            }
        }

        return rolePermissionMap;
    }

    private void updateLastRun(SystemVariable lastRun, Instant startTime) {
        if (!lastRun.getName().equals(LAST_RUN_SYSTEM_VARIABLE)) {
            throw new IllegalArgumentException("System variable name is NOT \"" + LAST_RUN_SYSTEM_VARIABLE + "\", but " + lastRun.getName());
        }
        lastRun.setInstantValue(startTime);
        systemVariableDAO.save(lastRun);
    }
}
