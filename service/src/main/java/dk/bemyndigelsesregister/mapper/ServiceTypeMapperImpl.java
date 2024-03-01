package dk.bemyndigelsesregister.mapper;

import dk.bemyndigelsesregister.domain.*;
import dk.bemyndigelsesregister.service.MetadataManager;
import dk.nsi.bemyndigelse._2017._08._01.DelegatingRole;
import dk.nsi.bemyndigelse._2017._08._01.ObjectFactory;
import dk.nsi.bemyndigelse._2017._08._01.State;
import dk.nsi.bemyndigelse._2017._08._01.SystemPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceTypeMapperImpl implements ServiceTypeMapper {
    @Autowired
    private MetadataManager metadataManager;

    private final ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public dk.nsi.bemyndigelse._2017._08._01.Delegation toDelegationType(Delegation delegation) {
        if (delegation == null)
            return null;

        Metadata metadata = metadataManager.getMetadata(Domain.DEFAULT_DOMAIN, delegation.getSystemCode());

        dk.nsi.bemyndigelse._2017._08._01.Delegation delegationType = objectFactory.createDelegation();

        delegationType.setDelegatorCpr(delegation.getDelegatorCpr());
        delegationType.setDelegateeCpr(delegation.getDelegateeCpr());
        delegationType.setDelegateeCvr(delegation.getDelegateeCvr());
        delegationType.setCreated(delegation.getCreated());
        delegationType.setState(State.fromValue(delegation.getState().value()));
        delegationType.setDelegationId(delegation.getCode());
        delegationType.setRole(toRole(metadata, delegation.getRoleCode()));
        delegationType.setSystem(toDelegatingSystem(metadata));
        delegationType.setEffectiveFrom(delegation.getEffectiveFrom());
        delegationType.setEffectiveTo(delegation.getEffectiveTo());

        boolean hasAsteriskPermission = delegation.hasAsteriskPermission();

        // permissions
        if (hasAsteriskPermission) {
            for (DelegatablePermission p : metadata.getDelegatablePermissions(delegation.getRoleCode())) {
                if (p.isDelegatable()) {
                    delegationType.getPermissions().add(toPermission(p.getPermission().getCode(), p.getPermission().getDescription()));
                }
            }
        } else {
            for (DelegationPermission delegationpermission : delegation.getDelegationPermissions()) {
                SystemPermission p = toPermission(metadata, delegationpermission);
                if (p != null) {
                    delegationType.getPermissions().add(p);
                }
            }

            // delegatable, but not delegated permissions
            for (DelegatablePermission delegatablePermission : metadata.getDelegatablePermissions(delegation.getRoleCode())) {
                if (delegatablePermission.isDelegatable()) {
                    boolean found = false;
                    for (DelegationPermission delegationPermission : delegation.getDelegationPermissions()) {
                        if (delegatablePermission.getPermission().getCode().equals(delegationPermission.getPermissionCode())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        delegationType.getNotDelegatedPermissions().add(toPermission(delegatablePermission.getPermission().getCode(), delegatablePermission.getPermission().getDescription()));
                    }
                }
            }
        }

        // undelegatable permissions
        for (Permission permission : metadata.getUndelegatablePermissions(delegation.getRoleCode())) {
            delegationType.getUndelegatablePermissions().add(toPermission(permission.getCode(), permission.getDescription()));
        }

        return delegationType;
    }

    private SystemPermission toPermission(Metadata metadata, DelegationPermission delegationPermission) {
        SystemPermission xmlPermission = null;

        if (delegationPermission != null) {
            Permission p = metadata.getPermission(delegationPermission.getPermissionCode());
            if (p != null) {
                xmlPermission = toPermission(p.getCode(), p.getDescription());
            }
        }

        return xmlPermission;
    }

    private SystemPermission toPermission(String permissionCode, String permissionDescription) {
        SystemPermission xmlPermission = objectFactory.createSystemPermission();

        xmlPermission.setPermissionId(permissionCode);
        xmlPermission.setPermissionDescription(permissionDescription);

        return xmlPermission;
    }

    private dk.nsi.bemyndigelse._2017._08._01.DelegatingSystem toDelegatingSystem(Metadata metadata) {
        DelegatingSystem ds = metadata.getSystem();
        if (ds == null)
            return null;

        dk.nsi.bemyndigelse._2017._08._01.DelegatingSystem xmlSystem = objectFactory.createDelegatingSystem();
        xmlSystem.setSystemId(ds.getCode());
        xmlSystem.setSystemLongName(ds.getDescription());

        return xmlSystem;
    }

    private dk.nsi.bemyndigelse._2017._08._01.DelegatingRole toRole(Metadata metadata, String roleCode) {
        Role r = metadata.getRole(roleCode);
        if (r == null)
            return null;

        dk.nsi.bemyndigelse._2017._08._01.DelegatingRole xmlRole = objectFactory.createDelegatingRole();
        xmlRole.setRoleId(r.getCode());
        xmlRole.setRoleDescription(r.getDescription());
        if (r.getEducationCodes() != null && !r.getEducationCodes().isEmpty()) {
            DelegatingRole.EducationCodes educationCodes = new DelegatingRole.EducationCodes();
            for (String educationCode : r.getEducationCodes()) {
                educationCodes.getEducationCodes().add(educationCode);
            }
            xmlRole.setEducationCodes(educationCodes);
        }

        return xmlRole;
    }
}
