package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Permission;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Role;
import dk.nsi.bemyndigelse._2016._01._01.ObjectFactory;
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
        delegationType.setDelegationId(delegation.getDomainId());
        delegationType.setRole(toRole(delegation.getDelegatingSystem(), delegation.getRole()));
        delegationType.setSystem(toSystem(delegation.getDelegatingSystem()));
        delegationType.setEffectiveFrom(toXmlGregorianCalendar(delegation.getEffectiveFrom()));
        delegationType.setEffectiveTo(toXmlGregorianCalendar(delegation.getEffectiveTo()));

        for (DelegationPermission permission : delegation.getDelegationPermissions())
            delegationType.getPermission().add(toPermission(delegation.getDelegatingSystem(), permission));

        return delegationType;
    }

    private Permission getPermission(String delegatingSystem, String domainId) {
        return delegatingSystem == null || domainId == null ? null : permissionDao.findByDomainId(delegatingSystem, domainId);
    }

    private DelegatingSystem getDelegatingSystem(String domainId) {
        return domainId == null ? null : delegatingSystemDao.findByDomainId(domainId);
    }

    private Role getRole(String delegatingSystem, String domainId) {
        if (delegatingSystem == null || domainId == null)
            return null;
        DelegatingSystem ds = getDelegatingSystem(delegatingSystem);
        return ds == null ? null : roleDao.findByDomainId(ds.getId(), domainId);
    }

    private dk.nsi.bemyndigelse._2016._01._01.SystemPermission toPermission(String delegatingSystem, DelegationPermission permission) {
        if (permission == null)
            return null;

        dk.nsi.bemyndigelse._2016._01._01.SystemPermission xmlPermission = objectFactory.createSystemPermission();
        xmlPermission.setPermissionId(permission.getPermissionId());
        Permission p = getPermission(delegatingSystem, permission.getPermissionId());
        xmlPermission.setPermissionDescription(p != null ? p.getDescription() : permission.getPermissionId());

        return xmlPermission;
    }

    private dk.nsi.bemyndigelse._2016._01._01.DelegatingSystem toSystem(String delegatingSystem) {
        DelegatingSystem ds = getDelegatingSystem(delegatingSystem);
        if (ds == null)
            return null;

        dk.nsi.bemyndigelse._2016._01._01.DelegatingSystem xmlSystem = objectFactory.createDelegatingSystem();
        xmlSystem.setSystemId(ds.getDomainId());
        xmlSystem.setSystemLongName(ds.getDescription());

        return xmlSystem;
    }

    private dk.nsi.bemyndigelse._2016._01._01.DelegatingRole toRole(String delegatingSystem, String role) {
        Role r = getRole(delegatingSystem, role);
        if (r == null)
            return null;

        dk.nsi.bemyndigelse._2016._01._01.DelegatingRole xmlRole = objectFactory.createDelegatingRole();
        xmlRole.setRoleId(r.getDomainId());
        xmlRole.setRoleDescription(r.getDescription());

        return xmlRole;
    }

    private XMLGregorianCalendar toXmlGregorianCalendar(DateTime dateTime) {
        if (dateTime == null)
            return null;
        return datatypeFactory.newXMLGregorianCalendar(new DateTime(dateTime, DateTimeZone.UTC).toGregorianCalendar());
    }
}
