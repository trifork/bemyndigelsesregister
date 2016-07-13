package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegatingSystemDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.SystemVariableDao;
import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.bemyndigelse._2016._01._01.State;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.*;

@Repository
public class MetadataChangeHandlerJob {
    private static final Logger logger = Logger.getLogger(MetadataChangeHandlerJob.class);
    private static final String LAST_RUN_SYSTEM_VARIABLE = "lastMetadataChangeHandlerRun";

    private Map<String, Map<String, Set<String>>> systemRolePermissionMap; // systemcode, rolecode, set of permissioncodes

    @Inject
    DelegatingSystemDao delegatingSystemDao;

    @Inject
    DelegationDao delegationDao;

    @Inject
    SystemService systemService;

    @Inject
    SystemVariableDao systemVariableDao;

    @Inject
    MetadataManager metadataManager;

    @Inject
    DelegationManager delegationManager;

    @Value("${metadatachangehandlerjob.enabled:true}")
    String jobEnabled;

    @Scheduled(cron = "${metadatachangehandlerjob.cron:30 0/5 * * * ?}")
    public void start() {
        if (Boolean.valueOf(jobEnabled)) {
            logger.info("MetadataChangeHandler job started");

            SystemVariable lastRun = systemVariableDao.getByName(LAST_RUN_SYSTEM_VARIABLE);
            if(lastRun == null)
                lastRun = new SystemVariable(LAST_RUN_SYSTEM_VARIABLE, DateTime.now());
            DateTime startTime = systemService.getDateTime();

            // update delegations with asterisk permission for changed systems
            handleChangedMetadata(startTime, lastRun.getDateTimeValue());

            updateLastRun(lastRun, startTime);
            logger.info("MetadataChangeHandler job ended");
        } else
            logger.info("MetadataChangeHandler job disabled");
    }


    public void handleChangedMetadata(DateTime startTime, DateTime lastRun) {
        systemRolePermissionMap = null;

        List<DelegatingSystem> systems = delegatingSystemDao.findByLastModifiedGreaterThanOrEquals(lastRun);

        if (systems == null || systems.isEmpty()) {
            logger.info("No metadata changed");
        } else {
            for (DelegatingSystem system : systems) {
                String hashSystemVariableName = system.getCode() + "Hash";
                String newHash = getRolePermissionHash(system.getCode());

                SystemVariable hashSystemVariable = systemVariableDao.getByName(hashSystemVariableName);
                String oldHash = hashSystemVariable != null ? hashSystemVariable.getValue() : "";

                if (newHash.equals(oldHash)) {
                    logger.info("Role/permission metadata hash [" + newHash + "] unchanged for system [" + system.getCode() + "], skipping delegation export");
                } else {
                    updateDelegationsWithAsteriskForSystem(startTime, system.getCode());

                    // save hash
                    if (hashSystemVariable == null) {
                        hashSystemVariable = new SystemVariable(hashSystemVariableName, newHash);
                    } else {
                        hashSystemVariable.setValue(newHash);
                    }
                    systemVariableDao.save(hashSystemVariable);

                    logger.info("Saved new role/permission metadata hash [" + newHash + "] for system [" + system.getCode() + "]");
                }
            }
        }

        systemRolePermissionMap = null;
    }

    private void updateDelegationsWithAsteriskForSystem(DateTime startTime, String systemCode) {
        logger.info("Processing system [" + systemCode + "]. *-permissions to delegate: ");

        Map<String, Set<String>> rolePermissionMap = getRolePermissionMap(systemCode);
        for (String roleCode : rolePermissionMap.keySet()) {
            logger.info("   [" + roleCode + "]: " + rolePermissionMap.get(roleCode));
        }

        List<Long> delegationIds = delegationDao.findWithAsterisk(systemCode, startTime);
        updatePermissionsForDelegations(startTime, systemCode, delegationIds);
    }

    private void updatePermissionsForDelegations(DateTime startTime, String systemCode, List<Long> delegationIds) {
        Metadata metadata = metadataManager.getMetadata(null, systemCode);
        if (metadata != null && metadata.getDelegatablePermissions() != null) {
            logger.info("Start updating permissions for " + delegationIds.size() + " delegations with *-permission");
            int updateCount = 0;
            for (Long delegationId : delegationIds) {
                Delegation delegation = delegationDao.get(delegationId);
                if (delegation.getState() == State.GODKENDT) {
                    boolean update = true;

                    if (delegation.getDelegationPermissions() != null) {
                        // check if permissions is unchanged

                        Set<String> permissionCodesDelegation = new HashSet<>();
                        for (DelegationPermission dp : delegation.getDelegationPermissions())
                            if (!Metadata.ASTERISK_PERMISSION_CODE.equals(dp.getPermissionCode()))
                                permissionCodesDelegation.add(dp.getPermissionCode());

                        Set<String> permissionCodesMetadata = new HashSet<>();
                        for (Metadata.DelegatablePermission dp : metadata.getDelegatablePermissions(delegation.getRoleCode()))
                            if (dp.isDelegatable() && !Metadata.ASTERISK_PERMISSION_CODE.equals(dp.getPermissionCode()))
                                permissionCodesMetadata.add(dp.getPermissionCode());

                        if (permissionCodesDelegation.equals(permissionCodesMetadata))
                            update = false;
                    }

                    if (update) {
                        // delegationManager will expand asterisk to new permissions
                        delegationManager.createDelegation(delegation.getSystemCode(), delegation.getDelegatorCpr(), delegation.getDelegateeCpr(), delegation.getDelegateeCvr(), delegation.getRoleCode(), State.GODKENDT, Collections.singletonList(Metadata.ASTERISK_PERMISSION_CODE), startTime, delegation.getEffectiveTo());
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

            Metadata metadata = metadataManager.getMetadata(null, systemCode);
            if (metadata != null && metadata.getDelegatablePermissions() != null) {
                for (Metadata.DelegatablePermission permission : metadata.getDelegatablePermissions()) {
                    if (permission.isDelegatable() && !permission.getPermissionCode().equals(Metadata.ASTERISK_PERMISSION_CODE)) { // asterisk permissions are not exported
                        Set<String> permissionCodes = rolePermissionMap.get(permission.getRoleCode());
                        if (permissionCodes == null) {
                            permissionCodes = new HashSet<>();
                            rolePermissionMap.put(permission.getRoleCode(), permissionCodes);
                        }
                        permissionCodes.add(permission.getPermissionCode());
                    }
                }
            }
        }

        return rolePermissionMap;
    }

    private void updateLastRun(SystemVariable lastRun, DateTime startTime) {
        if (!lastRun.getName().equals(LAST_RUN_SYSTEM_VARIABLE)) {
            throw new IllegalArgumentException("System variable name is NOT \"" + LAST_RUN_SYSTEM_VARIABLE + "\", but " + lastRun.getName());
        }
        lastRun.setDateTimeValue(startTime);
        systemVariableDao.save(lastRun);
    }
}
