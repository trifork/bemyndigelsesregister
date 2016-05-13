package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegatingSystemDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.SystemVariableDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.exportmodel.Delegations;
import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.bemyndigelse._2016._01._01.State;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.*;

@Repository
public class DelegationExportJob {
    private static Logger logger = Logger.getLogger(DelegationExportJob.class);

    @Inject
    DelegationDao delegationDao;

    @Inject
    DelegatingSystemDao delegatingSystemDao;

    @Inject
    SystemVariableDao systemVariableDao;

    @Inject
    SystemService systemService;

    @Inject
    MetadataManager metadataManager;

    @Inject
    @Named("nspManagerSftp")
    NspManager nspManager;

    @Value("${nsp.schema.version}")
    String nspSchemaVersion;

    @Value("${bemyndigelsesexportjob.enabled}")
    String jobEnabled;

    private Map<String, Map<String, Set<String>>> systemRolePermissionMap; // systemcode, rolecode, set of permissioncodes

    @Scheduled(cron = "${bemyndigelsesexportjob.cron}")
    public void startExport() throws IOException {
        if (Boolean.valueOf(jobEnabled)) {
            logger.info("DelegationExport job begun");

            systemRolePermissionMap = null;
            SystemVariable lastRun = systemVariableDao.getByName("lastRun");
            final DateTime startTime = systemService.getDateTime();

            // reexport delegations with asterisk permission for changed systems
            handleChangedMetadata(startTime, delegatingSystemDao.findByLastModifiedGreaterThanOrEquals(lastRun.getDateTimeValue()));

            // export individually changed delegations
            handleChangedDelegations(startTime, delegationDao.findByLastModifiedGreaterThanOrEquals(lastRun.getDateTimeValue()));

            updateLastRun(lastRun, startTime);
            systemRolePermissionMap = null;
            logger.info("DelegationExport job ended");
        } else
            logger.info("DelegationExport job disabled");
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
                    if (!permission.getPermissionCode().equals(Metadata.ASTERISK_PERMISSION_CODE)) { // asterisk permissions are not exported
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

    private void handleChangedMetadata(DateTime startTime, List<DelegatingSystem> systems) {
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
                    exportDelegationsWithAsteriskForSystem(startTime, system.getCode());

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
    }

    private void exportDelegationsWithAsteriskForSystem(DateTime startTime, String systemCode) {
        logger.info("Processing system [" + systemCode + "]. *-permissions to delegate: ");

        Map<String, Set<String>> rolePermissionMap = getRolePermissionMap(systemCode);
        for (String roleCode : rolePermissionMap.keySet()) {
            logger.info("   [" + roleCode + "]: " + rolePermissionMap.get(roleCode));
        }

        List<Long> delegationIds = delegationDao.findWithAsterisk(systemCode, startTime);
        if (delegationIds == null || delegationIds.isEmpty()) {
            logger.info("No delegations to with asterisk permission to export for system [" + systemCode + "]");
        } else {
            logger.info("Processing " + delegationIds.size() + " delegations with asterisk permission for system [" + systemCode + "]");
            int exportCount = 0;
            Delegations exportData = new Delegations();

            for (Long delegationId : delegationIds) {
                Delegation delegation = delegationDao.get(delegationId);
                if (delegation.getState() == State.GODKENDT && delegation.getEffectiveTo().isAfter(startTime)) {
                    exportData.addDelegation(delegation, rolePermissionMap.get(delegation.getRoleCode()));

                    if (++exportCount % 100 == 0) {
                        logger.info("  " + exportCount + " exported");
                    }
                }
            }

            if (exportCount > 0) {
                exportData.setDate(startTime.toString("yyyyMMdd"));
                exportData.setTimeStamp(startTime.toString("HHmmssSSS"));
                exportData.setVersion(nspSchemaVersion);

                nspManager.send(exportData, startTime);
            }

            logger.info("Exported " + exportCount + " of " + delegationIds.size() + " delegations for system [" + systemCode + "]");
        }
    }


    @Transactional
    public void completeExport() {
        SystemVariable lastRun = systemVariableDao.getByName("lastRun");
        DateTime startTime = systemService.getDateTime();
        try {
            handleChangedDelegations(startTime, delegationDao.findByLastModifiedGreaterThanOrEquals(new DateTime(1970, 1, 1, 0, 0)));
            updateLastRun(lastRun, startTime);
        } catch (IOException e) {
            logger.error("Export failed", e);
        }
    }

    public void handleChangedDelegations(DateTime startTime, List<Long> delegationIds) throws IOException {
        if (delegationIds == null || delegationIds.size() == 0) {
            logger.info("No changed delegations to export");
        } else {
            logger.info("Exporting " + delegationIds.size() + " changed delegations");

            Delegations exportData = new Delegations();
            for (Long delegationId : delegationIds) {
                Delegation delegation = delegationDao.get(delegationId);
                if (delegation.getState() == State.GODKENDT) {
                    Set<String> permissionCodes = new HashSet<>();
                    for (DelegationPermission delegationPermission : delegation.getDelegationPermissions()) {
                        permissionCodes.add(delegationPermission.getPermissionCode());
                    }

                    if (permissionCodes.contains(Metadata.ASTERISK_PERMISSION_CODE)) { // expand asterisk to all delegatable permissions for role
                        Map<String, Set<String>> rolePermissionMap = getRolePermissionMap(delegation.getSystemCode());
                        permissionCodes = rolePermissionMap.get(delegation.getRoleCode());
                    }

                    exportData.addDelegation(delegation, permissionCodes);
                }

            }
            exportData.setDate(startTime.toString("yyyyMMdd"));
            exportData.setTimeStamp(startTime.toString("HHmmssSSS"));
            exportData.setVersion(nspSchemaVersion);

            nspManager.send(exportData, startTime);

            logger.info("Completed export of changed delegations");
        }
    }

    private void updateLastRun(SystemVariable lastRun, DateTime startTime) {
        if (!lastRun.getName().equals("lastRun")) {
            throw new IllegalArgumentException("System variable name is NOT \"lastRun\", but " + lastRun.getName());
        }
        lastRun.setDateTimeValue(startTime);
        systemVariableDao.save(lastRun);
    }
}
