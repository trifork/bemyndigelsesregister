package dk.bemyndigelsesregister.batch.exportmodel;

import dk.bemyndigelsesregister.adapters.InstantAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Instant;

/**
 * For data exported by ExportJob
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class Delegation {
    @XmlElement(name = "kode")
    protected String uuid;

    @XmlElement(name = "bemyndigende_cpr")
    protected String delegatorCpr;

    @XmlElement(name = "bemyndigede_cpr")
    protected String delegateeCpr;

    @XmlElement(name = "bemyndigede_cvr")
    protected String delegateeCvr;

    @XmlElement
    protected String system;

    @XmlElement
    protected String status;

    @XmlElement(name = "arbejdsfunktion")
    protected String role;

    @XmlElement(name = "rettighed")
    protected String permission;

    @XmlElement(name = "godkendelsesdato")
    @XmlJavaTypeAdapter(value = InstantAdapter.class)
    protected Instant approvalDate;

    @XmlElement(name = "ModifiedDate")
    @XmlJavaTypeAdapter(value = InstantAdapter.class)
    protected Instant modifiedDate;

    @XmlElement(name = "ValidFrom")
    @XmlJavaTypeAdapter(value = InstantAdapter.class)
    protected Instant effectiveFrom;

    @XmlElement(name = "ValidTo")
    @XmlJavaTypeAdapter(value = InstantAdapter.class)
    protected Instant effectiveTo;

    public Delegation() {
    }

    public Delegation(String uuid, String delegatorCpr, String delegateeCpr, String delegateeCvr, String system, String status, String role, String permission, Instant approvalDate, Instant modifiedDate, Instant effectiveFrom, Instant effectiveTo) {
        this.uuid = uuid;
        this.delegatorCpr = delegatorCpr;
        this.delegateeCpr = delegateeCpr;
        this.delegateeCvr = delegateeCvr;
        this.system = system;
        this.status = status;
        this.role = role;
        this.permission = permission;
        this.approvalDate = approvalDate;
        this.modifiedDate = modifiedDate;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
    }
}
