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
    DelegationManager delegationManager;

    @Inject
    @Named("nspManagerSftp")
    NspManager nspManager;

    @Value("${nsp.schema.version}")
    String nspSchemaVersion;

    @Value("${bemyndigelsesexportjob.enabled:true}")
    String jobEnabled;

    @Value("${bemyndigelsesexportjob.batchsize:5000}")
    Integer batchSize;

    private Map<String, Map<String, Set<String>>> systemRolePermissionMap; // systemcode, rolecode, set of permissioncodes
    private int batchNo;

    @Scheduled(cron = "${bemyndigelsesexportjob.cron}")
    public void startExport() throws IOException {
        if (Boolean.valueOf(jobEnabled)) {
            logger.info("DelegationExport job started");

            systemRolePermissionMap = null;
            batchNo = 1;
            SystemVariable lastRun = systemVariableDao.getByName("lastRun");
            DateTime startTime = systemService.getDateTime();

            // reexport delegations with asterisk permission for changed systems
            handleChangedMetadata(startTime, delegatingSystemDao.findByLastModifiedGreaterThanOrEquals(lastRun.getDateTimeValue()));

            // export individually changed delegations
            exportChangedDelegations(startTime, delegationDao.findByLastModifiedGreaterThanOrEquals(lastRun.getDateTimeValue()));

            updateLastRun(lastRun, startTime);
            systemRolePermissionMap = null;
            logger.info("DelegationExport job ended");
        } else
            logger.info("DelegationExport job disabled");
    }

    @Transactional
    public void completeExport() {
        logger.info("Complete DelegationExport started");

        systemRolePermissionMap = null;
        batchNo = 1;
        SystemVariable lastRun = systemVariableDao.getByName("lastRun");
        DateTime startTime = systemService.getDateTime();

        exportChangedDelegations(startTime, delegationDao.findByLastModifiedGreaterThanOrEquals(new DateTime(1970, 1, 1, 0, 0)));
        updateLastRun(lastRun, startTime);

        logger.info("Complete DelegationExport ended");
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

    public void exportChangedDelegations(DateTime startTime, List<Long> delegationIds) {
        if (delegationIds == null || delegationIds.size() == 0) {
            logger.info("No changed delegations to export");
        } else {
            logger.info("Exporting " + delegationIds.size() + " changed delegations");

            // export in batches
            int startIndex = 0;
            while (startIndex < delegationIds.size()) {
                int endIndex = startIndex + batchSize;
                if (endIndex > delegationIds.size())
                    endIndex = delegationIds.size();

                logger.info("  Exporting batch " + batchNo + " (index " + startIndex + " - " + endIndex + ")");
                exportBatch(startTime, delegationIds.subList(startIndex, endIndex));

                startIndex = endIndex;
            }
        }
    }

    public void exportBatch(DateTime startTime, List<Long> delegationIds) {
        int exportCount = 0;

        Delegations exportData = new Delegations();
        for (Long delegationId : delegationIds) {
            Delegation delegation = delegationDao.get(delegationId);
            if (delegation.getState() == State.GODKENDT) {
                for (DelegationPermission delegationPermission : delegation.getDelegationPermissions()) {
                    if (!Metadata.ASTERISK_PERMISSION_CODE.equals(delegationPermission.getPermissionCode())) {
                        exportData.addDelegation(delegationPermission.getCode(), delegation.getDelegatorCpr(), delegation.getDelegateeCpr(), delegation.getDelegateeCvr(), delegation.getSystemCode(), delegation.getState().value(), delegation.getRoleCode(), delegationPermission.getPermissionCode(), delegation.getCreated(), delegation.getLastModified(), delegation.getEffectiveFrom(), delegation.getEffectiveTo());
                        exportCount++;
                    }
                }
            }
        }

        logger.info("    " + exportCount + " records exported");

        if (exportCount > 0) {
            exportData.setDate(startTime.toString("yyyyMMdd"));
            exportData.setTimeStamp(startTime.toString("HHmmssSSS"));
            exportData.setVersion(nspSchemaVersion);

            nspManager.send(exportData, startTime, batchNo);

            batchNo++; // Note: batchNo will only increase when delegations were actually exported
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
