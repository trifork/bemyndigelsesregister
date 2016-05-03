package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.MetadataManager;
import dk.nsi.bemyndigelse._2016._01._01.ObjectFactory;
import dk.nsi.bemyndigelse._2016._01._01.SystemPermission;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

@Repository
public class ServiceTypeMapperImpl implements ServiceTypeMapper {
    @Inject
    RoleDao roleDao;

    @Inject
    DelegatingSystemDao delegatingSystemDao;

    @Inject
    PermissionDao permissionDao;

    @Inject
    MetadataManager metadataManager;

    private static DatatypeFactory datatypeFactory;
    private ObjectFactory objectFactory = new ObjectFactory();

    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public dk.nsi.bemyndigelse._2016._01._01.Delegation toDelegationType(dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation delegation) {
        if (delegation == null)
            return null;

        dk.nsi.bemyndigelse._2016._01._01.Delegation delegationType = objectFactory.createDelegation();

        delegationType.setDelegatorCpr(delegation.getDelegatorCpr());
        delegationType.setDelegateeCpr(delegation.getDelegateeCpr());
        delegationType.setDelegateeCvr(delegation.getDelegateeCvr());
        delegationType.setCreated(toXmlGregorianCalendar(delegation.getCreated()));
        delegationType.setState(delegation.getState());
        delegationType.setDelegationId(delegation.getCode());
        delegationType.setRole(toRole(delegation.getSystemCode(), delegation.getRoleCode()));
        delegationType.setSystem(toSystem(delegation.getSystemCode()));
        delegationType.setEffectiveFrom(toXmlGregorianCalendar(delegation.getEffectiveFrom()));
        delegationType.setEffectiveTo(toXmlGregorianCalendar(delegation.getEffectiveTo()));


        boolean hasAsteriskPermission = delegation.hasAsteriskPermission();
        Metadata metadata = metadataManager.getMetadata(null, delegation.getSystemCode());

        // permissions
        if (hasAsteriskPermission) {
            for (Metadata.DelegatablePermission p : metadata.getDelegatablePermissions(delegation.getRoleCode())) {
                delegationType.getPermission().add(toPermission(p.getPermissionCode(), p.getPermissionDescription()));
            }
        } else {
            for (DelegationPermission permission : delegation.getDelegationPermissions()) {
                SystemPermission p = toPermission(delegation.getSystemCode(), permission);
                if (p != null) {
                    delegationType.getPermission().add(p);
                }
            }

            // delegatable, but not delegated permissions
            for (Metadata.DelegatablePermission delegatablePermission : metadata.getDelegatablePermissions(delegation.getRoleCode())) {
                if (delegatablePermission.isDelegatable()) {
                    boolean found = false;
                    for (DelegationPermission delegationPermission : delegation.getDelegationPermissions()) {
                        if (delegatablePermission.getPermissionCode().equals(delegationPermission.getPermissionCode())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        delegationType.getNotDelegatedPermission().add(toPermission(delegatablePermission.getPermissionCode(), delegatablePermission.getPermissionDescription()));
                    }
                }
            }
        }

        // undelegatable permissions
        for (Metadata.CodeAndDescription permission : metadata.getUndelegatablePermissions(delegation.getRoleCode())) {
            delegationType.getUndelegatablePermission().add(toPermission(permission.getCode(), permission.getDescription()));
        }

        return delegationType;
    }

    private Permission getPermission(String delegatingSystemCode, String code) {
        return delegatingSystemCode == null || code == null ? null : permissionDao.findByCode(delegatingSystemCode, code);
    }

    private DelegatingSystem getDelegatingSystem(String code) {
        return code == null ? null : delegatingSystemDao.findByCode(code);
    }

    private Role getRole(String delegatingSystemCode, String code) {
        if (delegatingSystemCode == null || code == null)
            return null;
        DelegatingSystem ds = getDelegatingSystem(delegatingSystemCode);
        return ds == null ? null : roleDao.findByCode(ds.getId(), code);
    }

    private dk.nsi.bemyndigelse._2016._01._01.SystemPermission toPermission(String delegatingSystemCode, DelegationPermission permission) {
        dk.nsi.bemyndigelse._2016._01._01.SystemPermission xmlPermission = null;

        if (permission != null) {
            Permission p = getPermission(delegatingSystemCode, permission.getPermissionCode());
            if (p != null) {
                xmlPermission = toPermission(p.getCode(), p.getDescription());
            }
        }

        return xmlPermission;
    }

    private dk.nsi.bemyndigelse._2016._01._01.SystemPermission toPermission(String permissionCode, String permissionDescription) {
        dk.nsi.bemyndigelse._2016._01._01.SystemPermission xmlPermission = objectFactory.createSystemPermission();

        xmlPermission.setPermissionId(permissionCode);
        xmlPermission.setPermissionDescription(permissionDescription);

        return xmlPermission;
    }

    private dk.nsi.bemyndigelse._2016._01._01.DelegatingSystem toSystem(String delegatingSystemCode) {
        DelegatingSystem ds = getDelegatingSystem(delegatingSystemCode);
        if (ds == null)
            return null;

        dk.nsi.bemyndigelse._2016._01._01.DelegatingSystem xmlSystem = objectFactory.createDelegatingSystem();
        xmlSystem.setSystemId(ds.getCode());
        xmlSystem.setSystemLongName(ds.getDescription());

        return xmlSystem;
    }

    private dk.nsi.bemyndigelse._2016._01._01.DelegatingRole toRole(String delegatingSystemCode, String roleCode) {
        Role r = getRole(delegatingSystemCode, roleCode);
        if (r == null)
            return null;

        dk.nsi.bemyndigelse._2016._01._01.DelegatingRole xmlRole = objectFactory.createDelegatingRole();
        xmlRole.setRoleId(r.getCode());
        xmlRole.setRoleDescription(r.getDescription());

        return xmlRole;
    }

    private XMLGregorianCalendar toXmlGregorianCalendar(DateTime dateTime) {
        if (dateTime == null)
            return null;
        return datatypeFactory.newXMLGregorianCalendar(new DateTime(dateTime, DateTimeZone.UTC).toGregorianCalendar());
    }
}
