package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import dk.nsi.bemyndigelse._2016._01._01.State;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
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
    protected String systemCode;

    @Column(name = "arbejdsfunktion_kode")
    protected String roleCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    protected State state;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "delegation")
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

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
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
}
