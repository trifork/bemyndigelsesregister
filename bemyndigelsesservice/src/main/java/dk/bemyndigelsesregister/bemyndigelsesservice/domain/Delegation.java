package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import dk.nsi.bemyndigelser._2012._04.Bemyndigelser;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * BEM 2.0 bemyndigelse
 * Created by obj on 02-02-2016.
 */

@Entity
@Table(name="bemyndigelse20")
public class Delegation extends ExternalIdentifiedDomainObject {
    private static DatatypeFactory datatypeFactory;
    static {
        try {
            datatypeFactory= DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
    protected String delegationId;
    protected String delegatorCpr;
    protected String delegateeCpr;
    protected String delegateeCvr;
    protected DelegatingSystem delegatingSystem;
    protected Role role;

    @Enumerated(EnumType.STRING)
    protected State state;

    @OneToMany(cascade = CascadeType.ALL)
    protected Set<DelegationPermission> delegationPermissions;
    protected DateTime created;
    protected DateTime effectiveFrom;
    protected DateTime effectiveTo;
    private int versionsid;

    public Delegation() {
    }

    public String getDelegationId() {
        return delegationId;
    }

    public void setDelegationId(String value) {
        this.delegationId = value;
    }

    public String getDelegatorCpr() {
        return delegateeCpr;
    }

    public void setDelegatorCpr(String value) {
        this.delegatorCpr = value;
    }

    public String getDelegateeCpr() {
        return delegateeCpr;
    }

    public void setDelegateeCpr(String value) {
        this.delegateeCpr = value;
    }

    public String getDelegateeCvr() {
        return delegateeCvr;
    }
    
    public void setDelegateeCvr(String value) {
        this.delegateeCvr = value;
    }
    
    public DelegatingSystem getDelegatingSystem() {
        return delegatingSystem;
    }
    
    public void setDelegatingSystem(DelegatingSystem value) {
        this.delegatingSystem = value;
    }

    public Role getRole() {
        return role;
    }
    
    public void setRole(Role value) {
        this.role = value;
    }
    
    public State getState() {
        return state;
    }

    public void setState(State value) {
        this.state = value;
    }

    public Set<DelegationPermission> getPermissions() {
        if (delegationPermissions == null) {
            delegationPermissions = new TreeSet<>();
        }
        return this.delegationPermissions;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime value) {
        this.created = value;
    }

    public DateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(DateTime value) {
        this.effectiveFrom = value;
    }

    public DateTime getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(DateTime value) {
        this.effectiveTo = value;
    }

    public int getVersionsid() {
        return versionsid;
    }

    public void setVersionsid(int versionsid) {
        this.versionsid = versionsid;
    }


    @Override
    public String toString() {
        Set<DelegationPermission> permissions = this.getPermissions();
        for (DelegationPermission permission : permissions)
            java.lang.System.out.println(permission.getPermissionId());

        return "Bemyndigelse20{" +
                "bemyndigendeCpr='" + delegatorCpr + '\'' +
                ", bemyndigendeCvr='" + delegateeCpr + '\'' +
                ", bemyndigedeCvr='" + delegateeCvr + '\'' +
                ", system='" + delegatingSystem + '\'' +
                ", arbejdsfunktion='" + role + '\'' +
                ", rettigheder=" + permissions +
                ", status=" + state +
                ", godkendelsesdato=" + created +
                ", gyldigFra=" + effectiveFrom +
                ", gyldigTil=" + effectiveTo +
                ", versionsid=" + versionsid +
                '}';
    }

    /**
     * Bruges tilsyneladende kun til stamdata eksportering
     * TODO hvorfor er så mange felter null?
     * @return
     */
    public List<Bemyndigelser.Bemyndigelse> toBemyndigelseType() {
        List<Bemyndigelser.Bemyndigelse> bemyndigelser = new LinkedList<>();
        for (DelegationPermission permission : delegationPermissions) {
            Bemyndigelser.Bemyndigelse type = new Bemyndigelser.Bemyndigelse();
            type.setBemyndigedeCpr(delegatorCpr);
            type.setBemyndigedeCvr(delegateeCpr);
            type.setBemyndigendeCpr(delegateeCvr);
            type.setCreatedDate(toXmlGregorianCalendar(created));
            type.setGodkendelsesdato(state==State.OPRETTET ? toXmlGregorianCalendar(created) : null);
            type.setStatus(state == State.OPRETTET ? "Godkendt" : "Bestilt");
            type.setKode(getKode());
            type.setModifiedDate(toXmlGregorianCalendar(sidstModificeret));
            type.setArbejdsfunktion(role.getId());
            type.setRettighed(permission.getPermissionId());
            type.setSystem(delegatingSystem.getId());
            type.setValidFrom(toXmlGregorianCalendar(effectiveFrom));
            type.setValidTo(toXmlGregorianCalendar(effectiveTo));
        }
        return bemyndigelser;
    }

    public dk.nsi.bemyndigelse._2016._01._01.Delegation toDelegationType() {
        dk.nsi.bemyndigelse._2016._01._01.Delegation type = new dk.nsi.bemyndigelse._2016._01._01.Delegation();
        type.setDelegatorCpr(delegatorCpr);
        type.setDelegatorCpr(delegateeCpr);
        type.setDelegateeCvr(delegateeCvr);
        type.setCreated(toXmlGregorianCalendar(created));
        if (state == State.OPRETTET) {
            type.setState(dk.nsi.bemyndigelse._2016._01._01.State.OPRETTET);
        } else {
            type.setState(dk.nsi.bemyndigelse._2016._01._01.State.ANMODET);
        }
        type.setDelegationId(getId());
        dk.nsi.bemyndigelse._2016._01._01.Role xmlRole = new dk.nsi.bemyndigelse._2016._01._01.Role();
        xmlRole.setRoleId(role.getId());
        xmlRole.setRoleDescription(role.getDescription());
        type.setRole(xmlRole);
        List<Bemyndigelser.Bemyndigelse> delegation = new LinkedList<>();
        for (DelegationPermission permission : delegationPermissions) {
            dk.nsi.bemyndigelse._2016._01._01.Permission xmlPermission = new dk.nsi.bemyndigelse._2016._01._01.Permission();
            xmlPermission.setPermissionId(permission.getPermissionId());
            // TODO KRS fix getting permissions from permissionId in delegationPermissions
            xmlPermission.setPermissionDescription("// TODO implementer at hente permissions fra id");
        }
        dk.nsi.bemyndigelse._2016._01._01.System xmlSystem = new dk.nsi.bemyndigelse._2016._01._01.System();
        xmlSystem.setSystemId(delegatingSystem.getId());
        xmlSystem.setSystemLongName("TODO implementer langt navn");
        type.setSystem(xmlSystem);
        type.setEffectiveFrom(toXmlGregorianCalendar(effectiveFrom));
        type.setEffectiveTo(toXmlGregorianCalendar(effectiveTo));
        return type;
    }

    private XMLGregorianCalendar toXmlGregorianCalendar(DateTime dateTime) {
        return datatypeFactory.newXMLGregorianCalendar(new DateTime(dateTime, DateTimeZone.UTC).toGregorianCalendar());
    }

}
