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

/**
 * BEM 2.0 bemyndigelse
 * Created by obj on 02-02-2016.
 */

@Entity
@Table(name = "bemyndigelse20")
public class Delegation extends ExternalIdentifiedDomainObject {
    private static DatatypeFactory datatypeFactory;

    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Column(name = "bemyndigende_cpr")
    protected String delegatorCpr;
    @Column(name = "bemyndigede_cpr")
    protected String delegateeCpr;
    @Column(name = "bemyndigede_cvr")
    protected String delegateeCvr;

    @Column(name = "linked_system_kode")
    protected String delegatingSystem;

    @Column(name = "arbejdsfunktion_kode")
    protected String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    protected State state;

    @OneToMany(cascade = CascadeType.ALL)
    @Column(name = "bemyndigelse20_id")
    protected Set<DelegationPermission> delegationPermissions;

    @Column(name = "godkendelsesdato")
    protected DateTime created;

    @Column(name = "gyldig_fra")
    protected DateTime effectiveFrom;

    @Column(name = "gyldig_til")
    protected DateTime effectiveTo;

    private int versionsid;

    public Delegation() {
    }

    public String getDelegatorCpr() {
        return delegatorCpr;
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

    public String getDelegatingSystem() {
        return delegatingSystem;
    }

    public void setDelegatingSystem(String delegatingSystem) {
        this.delegatingSystem = delegatingSystem;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public State getState() {
        return state;
    }

    public void setState(State value) {
        this.state = value;
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

    public Set<DelegationPermission> getDelegationPermissions() {
        return delegationPermissions;
    }

    public void setDelegationPermissions(Set<DelegationPermission> delegationPermissions) {
        this.delegationPermissions = delegationPermissions;
    }

    @Override
    public String toString() {
        return "Delegation{" +
                "delegatorCpr='" + delegatorCpr + '\'' +
                ", delegateeCpr='" + delegateeCpr + '\'' +
                ", delegateeCvr='" + delegateeCvr + '\'' +
                ", delegatingSystem='" + delegatingSystem + '\'' +
                ", role='" + role + '\'' +
                ", state=" + state +
                ", delegationPermissions=" + delegationPermissions +
                ", created=" + created +
                ", effectiveFrom=" + effectiveFrom +
                ", effectiveTo=" + effectiveTo +
                ", versionsid=" + versionsid +
                '}';
    }

    /**
     * Bruges tilsyneladende kun til stamdata eksportering
     * TODO hvorfor er så mange felter null?
     *
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
            type.setGodkendelsesdato(state == State.GODKENDT ? toXmlGregorianCalendar(created) : null);
            type.setStatus(state == State.GODKENDT ? "Godkendt" : "Bestilt");
            type.setKode(getDomainId());
            type.setModifiedDate(toXmlGregorianCalendar(sidstModificeret));
            type.setArbejdsfunktion(role);
            type.setRettighed(permission.getPermissionId());
            type.setSystem(delegatingSystem);
            type.setValidFrom(toXmlGregorianCalendar(effectiveFrom));
            type.setValidTo(toXmlGregorianCalendar(effectiveTo));
        }
        return bemyndigelser;
    }

    public dk.nsi.bemyndigelse._2016._01._01.Delegation toDelegationType() {
        dk.nsi.bemyndigelse._2016._01._01.Delegation type = new dk.nsi.bemyndigelse._2016._01._01.Delegation();
        type.setDelegatorCpr(delegatorCpr);
        type.setDelegateeCpr(delegateeCpr);
        type.setDelegateeCvr(delegateeCvr);
        type.setCreated(toXmlGregorianCalendar(created));
        type.setState(state == State.GODKENDT ? dk.nsi.bemyndigelse._2016._01._01.State.GODKENDT : dk.nsi.bemyndigelse._2016._01._01.State.BESTILT);
        type.setDelegationId(getDomainId());
        dk.nsi.bemyndigelse._2016._01._01.Role xmlRole = new dk.nsi.bemyndigelse._2016._01._01.Role();
        xmlRole.setRoleId(role);
//        xmlRole.setRoleDescription(role.getDescription()); TODO OBJ fix
        type.setRole(xmlRole);
        List<Bemyndigelser.Bemyndigelse> delegation = new LinkedList<>();
        for (DelegationPermission permission : delegationPermissions) {
            dk.nsi.bemyndigelse._2016._01._01.Permission xmlPermission = new dk.nsi.bemyndigelse._2016._01._01.Permission();
            xmlPermission.setPermissionId(permission.getPermissionId());
            // TODO KRS fix getting permissions from permissionId in delegationPermissions
            xmlPermission.setPermissionDescription("// TODO implementer at hente permissions fra id");
        }
        dk.nsi.bemyndigelse._2016._01._01.System xmlSystem = new dk.nsi.bemyndigelse._2016._01._01.System();
        xmlSystem.setSystemId(delegatingSystem);
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
