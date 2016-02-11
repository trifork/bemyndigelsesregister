package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.nsi.bemyndigelse._2012._05._01.Arbejdsfunktioner;
import dk.nsi.bemyndigelse._2012._05._01.DelegerbarRettigheder;
import dk.nsi.bemyndigelse._2012._05._01.Rettigheder;
import dk.nsi.bemyndigelse._2016._01._01.Delegation;
import dk.nsi.bemyndigelse._2016._01._01.ObjectFactory;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    public Arbejdsfunktioner toJaxbArbejdsfunktioner(final Collection<Arbejdsfunktion> arbejdsfunktionList) {
        return new Arbejdsfunktioner() {{
            getArbejdsfunktion().addAll(CollectionUtils.collect(
                    arbejdsfunktionList,
                    new Transformer<dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion, Arbejdsfunktion>() {
                        @Override
                        public Arbejdsfunktion transform(final dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion that) {
                            return new Arbejdsfunktion() {{
                                this.setArbejdsfunktion(that.getKode());
                                this.setBeskrivelse(that.getBeskrivelse());
                                this.setDomaene(that.getLinkedSystem().getDomaene().getKode());
                                this.setSystem(that.getLinkedSystem().getKode());
                            }};
                        }
                    }
            ));
        }};
    }

    @Override
    public Rettigheder toJaxbRettigheder(final Collection<Rettighed> rettighedList) {
        return new Rettigheder() {{
            getRettighed().addAll(CollectionUtils.collect(
                    rettighedList,
                    new Transformer<dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed, Rettighed>() {
                        @Override
                        public Rettighed transform(final dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed that) {
                            return new Rettighed() {{
                                this.setBeskrivelse(that.getBeskrivelse());
                                this.setDomaene(that.getLinkedSystem().getDomaene().getKode());
                                this.setRettighed(that.getKode());
                                this.setSystem(that.getLinkedSystem().getKode());
                            }};
                        }
                    }
            ));
        }};
    }

    @Override
    public DelegerbarRettigheder toJaxbDelegerbarRettigheder(final Collection<DelegerbarRettighed> delegerbarRettighedList) {
        return new DelegerbarRettigheder() {{
            getDelegerbarRettighed().addAll(CollectionUtils.collect(
                    delegerbarRettighedList,
                    new Transformer<dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegerbarRettighed, DelegerbarRettighed>() {
                        @Override
                        public DelegerbarRettighed transform(final dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegerbarRettighed that) {
                            return new DelegerbarRettighed() {{
                                this.setArbejdsfunktion(that.getArbejdsfunktion().getKode());
                                this.setDomaene(that.getArbejdsfunktion().getLinkedSystem().getDomaene().getKode());
                                this.setRettighed(that.getRettighedskode().getKode());
                                this.setSystem(that.getArbejdsfunktion().getLinkedSystem().getKode());
                            }};
                        }
                    }
            ));
        }};
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
        delegationType.setState(delegation.getState() == State.GODKENDT ? dk.nsi.bemyndigelse._2016._01._01.State.GODKENDT : dk.nsi.bemyndigelse._2016._01._01.State.BESTILT);
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

    private dk.nsi.bemyndigelse._2016._01._01.Permission toPermission(String delegatingSystem, DelegationPermission permission) {
        if (permission == null)
            return null;

        dk.nsi.bemyndigelse._2016._01._01.Permission xmlPermission = objectFactory.createPermission();
        xmlPermission.setPermissionId(permission.getPermissionId());
        Permission p = getPermission(delegatingSystem, permission.getPermissionId());
        xmlPermission.setPermissionDescription(p != null ? p.getDescription() : permission.getPermissionId());

        return xmlPermission;
    }

    private dk.nsi.bemyndigelse._2016._01._01.System toSystem(String delegatingSystem) {
        DelegatingSystem ds = getDelegatingSystem(delegatingSystem);
        if (ds == null)
            return null;

        dk.nsi.bemyndigelse._2016._01._01.System xmlSystem = objectFactory.createSystem();
        xmlSystem.setSystemId(ds.getDomainId());
        xmlSystem.setSystemLongName(ds.getDescription());

        return xmlSystem;
    }

    private dk.nsi.bemyndigelse._2016._01._01.Role toRole(String delegatingSystem, String role) {
        Role r = getRole(delegatingSystem, role);
        if (r == null)
            return null;

        dk.nsi.bemyndigelse._2016._01._01.Role xmlRole = objectFactory.createRole();
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
