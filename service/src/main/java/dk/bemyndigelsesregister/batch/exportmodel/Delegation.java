package dk.bemyndigelsesregister.batch.exportmodel;

import dk.bemyndigelsesregister.util.DateUtils;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
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
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar approvalDate;

    @XmlElement(name = "ModifiedDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar modifiedDate;

    @XmlElement(name = "ValidFrom")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar effectiveFrom;

    @XmlElement(name = "ValidTo")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar effectiveTo;

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
        this.approvalDate = DateUtils.toXmlGregorianCalendar(approvalDate);
        this.modifiedDate = DateUtils.toXmlGregorianCalendar(modifiedDate);
        this.effectiveFrom = DateUtils.toXmlGregorianCalendar(effectiveFrom);
        this.effectiveTo = DateUtils.toXmlGregorianCalendar(effectiveTo);
    }
}
